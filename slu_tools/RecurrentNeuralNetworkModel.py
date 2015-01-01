# Copied from https://github.com/mesnilgr/is13 : elman.py : Jan 1, 2015

import theano
import numpy
import time
import sys
import random
import subprocess
from theano import tensor as T
from collections import OrderedDict


class RecurrentNeuralNetworkModel(object):
    def __init__(self, nh, nc, ne, de, cs):
        """
        nh :: dimension of the hidden layer
        nc :: number of classes
        ne :: number of word embeddings in the vocabulary
        de :: dimension of the word embeddings
        cs :: word window context size
        """

        # parameters of the model
        self.emb = theano.shared(0.2 * numpy.random.uniform(-1.0, 1.0, (ne + 1, de)).
                                 astype(theano.config.floatX))  # add one for PADDING at the end
        self.Wx = theano.shared(0.2 * numpy.random.uniform(-1.0, 1.0, (de * cs, nh)).astype(theano.config.floatX))
        self.Wh = theano.shared(0.2 * numpy.random.uniform(-1.0, 1.0, (nh, nh)).astype(theano.config.floatX))
        self.W = theano.shared(0.2 * numpy.random.uniform(-1.0, 1.0, (nh, nc)).astype(theano.config.floatX))
        self.bh = theano.shared(numpy.zeros(nh, dtype=theano.config.floatX))
        self.b = theano.shared(numpy.zeros(nc, dtype=theano.config.floatX))
        self.h0 = theano.shared(numpy.zeros(nh, dtype=theano.config.floatX))

        # bundle
        self.params = [self.emb, self.Wx, self.Wh, self.W, self.bh, self.b, self.h0]
        self.names = ['embeddings', 'Wx', 'Wh', 'W', 'bh', 'b', 'h0']

        idxs = T.imatrix()  # as many columns as context window size/lines as words in the sentence
        x = self.emb[idxs].reshape((idxs.shape[0], de * cs))
        y = T.iscalar('y')  # label

        def recurrence(x_t, h_tm1):
            h_t = T.nnet.sigmoid(T.dot(x_t, self.Wx) + T.dot(h_tm1, self.Wh) + self.bh)
            s_t = T.nnet.softmax(T.dot(h_t, self.W) + self.b)
            return [h_t, s_t]

        [h, s], _ = theano.scan(fn=recurrence, sequences=x, outputs_info=[self.h0, None], n_steps=x.shape[0])
        p_y_given_x_lastword = s[-1, 0, :]
        p_y_given_x_sentence = s[:, 0, :]
        y_pred = T.argmax(p_y_given_x_sentence, axis=1)

        # cost and gradients and learning rate
        learning_rate = T.scalar('lr')
        nll = -T.mean(T.log(p_y_given_x_lastword)[y])
        gradients = T.grad(nll, self.params)
        updates = OrderedDict(( p, p - learning_rate * g ) for p, g in zip(self.params, gradients))

        # theano functions
        self.classify = theano.function(inputs=[idxs], outputs=y_pred)
        self.sentence_train = theano.function(inputs=[idxs, y, learning_rate],
                                              outputs=nll,
                                              updates=updates)

        normalization_updates = {self.emb: self.emb / T.sqrt((self.emb ** 2).sum(axis=1)).dimshuffle(0, 'x')}
        self.normalize = theano.function(inputs=[], updates=normalization_updates)

    def train(self, train_set, valid_set, settings):
        # train with early stopping on validation set

        train_lex, train_ne, train_y = train_set
        valid_lex, valid_ne, valid_y = valid_set

        nsentences = len(train_lex)

        best_accuracy = -1
        settings['clr'] = settings['lr']
        for e in xrange(settings['nepochs']):
            # shuffle
            shuffle([train_lex, train_ne, train_y], settings['seed'])
            settings['ce'] = e
            tic = time.time()
            for i in xrange(nsentences):
                cwords = context_window(train_lex[i], settings['win'])
                words = map(lambda x: numpy.asarray(x).astype('int32'), minibatch(cwords, settings['bs']))
                labels = train_y[i]
                for word_batch, label_last_word in zip(words, labels):
                    self.sentence_train(word_batch, label_last_word, settings['clr'])
                    self.normalize()
                if settings['verbose']:
                    print '[learning] epoch %i >> %2.2f%%' % (
                        e, (i + 1) * 100. / nsentences), 'completed in %.2f (sec) <<\r' % (time.time() - tic),
                    sys.stdout.flush()

            # evaluation
            predictions_valid = [self.classify(numpy.asarray(context_window(x, settings['win'])).astype('int32'))
                                 for x in valid_lex]

            validation_accuracy = evaluate_tagging(predictions_valid, valid_y)

            if validation_accuracy > best_accuracy:
                best_accuracy = validation_accuracy
                if settings['verbose']:
                    print 'NEW BEST: epoch', e, 'validation accuracy', validation_accuracy
                settings['be'] = e

            # learning rate decay if no improvement in 10 epochs
            if settings['decay'] and abs(settings['be'] - settings['ce']) >= 10:
                settings['clr'] *= 0.5
            if settings['clr'] < 1e-5:
                break

        print 'BEST RESULT: epoch', e, 'validation accuracy', validation_accuracy


def evaluate_tagging(predictions, ground_truth):
    """
    :param predictions: list of predicted tokens (must be same length as ground_truth)
    :param ground_truth: list of correct tokens
    :return: fraction of predicted tags which are correct
    """
    return 1.0 * len([i for i in xrange(len(ground_truth)) if ground_truth[i]==predictions[i]]) / len(ground_truth)


def shuffle(lol, seed):
    """
    lol :: list of list as input
    seed :: seed the shuffling

    shuffle inplace each list in the same order
    """
    for l in lol:
        random.seed(seed)
        random.shuffle(l)


def context_window(token_sequence, win):
    """
    win :: int corresponding to the size of the window
    given a list of indexes composing a sentence

    l :: array containing the word indexes

    it will return a list of list of indexes corresponding
    to context windows surrounding each word in the sentence
    """
    assert (win % 2) == 1
    assert win >= 1
    token_sequence = list(token_sequence)

    padded_token_sequence = win//2 * [-1] + token_sequence + win//2 * [-1]
    out = [padded_token_sequence[i:i+win] for i in range(len(token_sequence))]

    assert len(out) == len(token_sequence)
    return out


