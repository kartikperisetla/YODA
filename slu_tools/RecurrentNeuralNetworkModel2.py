# Copied from https://github.com/mesnilgr/is13 : elman.py : Jan 1, 2015

import theano
import numpy
import time
import sys
import random
import subprocess
from theano import tensor as T
from collections import OrderedDict


class RecurrentNeuralNetworkModel2(object):
    def __init__(self, dx, dc, nh, nc, cs):
        """
        dx :: dimension of input feature vector per token
        dc :: dimension of the context feature vector
        nh :: dimension of the hidden layer
        nc :: number of classes
        cs :: word window context size
        """
        self.cs = cs

        # parameters of the model
        self.Wx = theano.shared(0.2 * numpy.random.uniform(-1.0, 1.0, (dx * cs + dc, nh)).astype(theano.config.floatX))
        self.Wh = theano.shared(0.2 * numpy.random.uniform(-1.0, 1.0, (nh, nh)).astype(theano.config.floatX))
        self.W = theano.shared(0.2 * numpy.random.uniform(-1.0, 1.0, (nh, nc)).astype(theano.config.floatX))
        self.bh = theano.shared(numpy.zeros(nh, dtype=theano.config.floatX))
        self.b = theano.shared(numpy.zeros(nc, dtype=theano.config.floatX))
        self.h0 = theano.shared(numpy.zeros(nh, dtype=theano.config.floatX))

        # bundle
        self.params = [self.Wx, self.Wh, self.W, self.bh, self.b, self.h0]
        self.names = ['Wx', 'Wh', 'W', 'bh', 'b', 'h0']

        # x is the token features from the window
        x = T.dmatrix('x')
        y = T.iscalar('y')

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
        self.classify = theano.function(inputs=[x], outputs=y_pred)
        self.sentence_train = theano.function(inputs=[x, y, learning_rate],
                                              outputs=nll,
                                              updates=updates)

    def predict(self, token_features, context_features):
        x = context_window(token_features, self.cs) + context_features
        return self.classify(numpy.asarray(x).astype('int32'))

    def train(self, train_set, valid_set, settings):
        # train with early stopping on validation set

        x_train, y_train = train_set
        x_validate, y_validate = valid_set

        n_training_sentences = len(x_train)

        best_accuracy = -1
        settings['current_learning_rate'] = settings['lr']
        for e in xrange(settings['n_epochs']):
            # shuffle
            shuffle([x_train, y_train], settings['seed'])
            settings['current_epoch'] = e
            tic = time.time()
            for i in xrange(n_training_sentences):
                context_words = context_window(x_train[i], settings['win'])
                words = map(lambda x: numpy.asarray(x).astype('int32'), mini_batch(context_words, settings['bs']))
                labels = y_train[i]
                for word_batch, label_last_word in zip(words, labels):
                    self.sentence_train(word_batch, label_last_word, settings['current_learning_rate'])
                if settings['verbose']:
                    print '[learning] epoch %i >> %2.2f%%' % (
                        e, (i + 1) * 100. / n_training_sentences), 'completed in %.2f (sec) <<\r' % (time.time() - tic),
                    sys.stdout.flush()

            # evaluation
            predictions_valid = [self.classify(numpy.asarray(context_window(x, settings['win'])).astype('int32'))
                                 for x in x_validate]

            validation_accuracy = numpy.mean(
                [evaluate_tagging(predictions_valid[k], y_validate[k]) for k in range(len(predictions_valid))])

            if validation_accuracy > best_accuracy:
                best_accuracy = validation_accuracy
                if settings['verbose']:
                    print 'NEW BEST: epoch', e, 'validation accuracy', validation_accuracy
                settings['best_epoch'] = e

            # learning rate decay if no improvement in 10 epochs
            if settings['decay'] and abs(settings['best_epoch'] - settings['current_epoch']) >= 10:
                settings['current_learning_rate'] *= 0.99
            if settings['current_learning_rate'] < 1e-5:
                break

        print 'BEST RESULT: epoch', e, 'validation accuracy', best_accuracy


def evaluate_tagging(predictions, ground_truth):
    """
    :param predictions: list of predicted tokens (must be same length as ground_truth)
    :param ground_truth: list of correct tokens
    :return: fraction of predicted tags which are correct
    """
    # print "evaluating tagging:"
    # print ground_truth
    # print predictions
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


def mini_batch(l, bs):
    """
    l :: list of word idxs
    return a list of minibatches of indexes
    which size is equal to bs
    border cases are treated as follow:
    eg: [0,1,2,3] and bs = 3
    will output:
    [[0],[0,1],[0,1,2],[1,2,3]]
    """
    out = [l[:i] for i in xrange(1, min(bs,len(l)+1) )]
    out += [l[i-bs:i] for i in xrange(bs,len(l)+1) ]
    assert len(l) == len(out)
    return out


def context_window(token_vector_sequence, win):
    """
    win :: int corresponding to the size of the window
    given a list of indexes composing a sentence
    """
    assert (win % 2) == 1
    assert win >= 1
    token_vector_size = len(token_vector_sequence[0])
    padded_token_sequence = win//2 * [[0]*token_vector_size] + token_vector_sequence + win//2 * [[0]*token_vector_size]
    print padded_token_sequence
    out = [reduce(lambda x, y: x+y, padded_token_sequence[i:i+win], []) for i in range(len(token_vector_sequence))]
    assert len(out) == len(token_vector_sequence)
    return out


