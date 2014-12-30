# Copied algorithm and Theano graph structure from: http://www.deeplearning.net/tutorial/ on Dec 30, 2014
import time
import numpy

import theano
import theano.tensor as T

learning_rate = 0.13
n_epochs = 1000
batch_size = 10


class LogisticRegressionModel(object):
    def __init__(self, n_in, n_out):

        self.x = T.matrix('x')  # input feature vectors
        self.y = T.ivector('y')  # labels, presented as 1D vector of [int] labels

        # initialize with 0 the weights W as a matrix of shape (n_in, n_out)
        self.W = theano.shared(
            value=numpy.zeros(
                (n_in, n_out),
                dtype=theano.config.floatX
            ),
            name='W',
            borrow=True
        )
        # initialize the biases b as a vector of n_out 0s
        self.b = theano.shared(
            value=numpy.zeros(
                (n_out,),
                dtype=theano.config.floatX
            ),
            name='b',
            borrow=True
        )

        # expression for computing the matrix of class-membership probabilities
        self.p_y_given_x = T.nnet.softmax(T.dot(self.x, self.W) + self.b)

        # description of how to compute prediction as class whose probability is maximal
        self.y_predicted = T.argmax(self.p_y_given_x, axis=1)

        # function to compute the output probabilities
        self.output_probabilities = theano.function([self.x], self.p_y_given_x)

        # parameters of the model
        self.params = [self.W, self.b]

    def train(self, dataset):
        train_set_x, train_set_y = dataset[0]
        valid_set_x, valid_set_y = dataset[1]

        # compute number of minibatches for training, validation and testing
        n_train_batches = train_set_x.get_value(borrow=True).shape[0] / batch_size
        n_valid_batches = valid_set_x.get_value(borrow=True).shape[0] / batch_size
        # n_train_batches = train_set_x.shape[0] / batch_size
        # n_valid_batches = valid_set_x.shape[0] / batch_size


        # negative log likelihood of the data set
        cost = -T.mean(T.log(self.p_y_given_x)[T.arange(self.y.shape[0]), self.y])

        index = T.lscalar()  # index to a [mini]batch

        print "valid_set_x.shape:", valid_set_x.shape
        print "index:", index
        print "batch size:", batch_size
        print "index*batch size", index*batch_size
        print valid_set_x[0: 1]

        validate_model = theano.function(
            inputs=[index],
            outputs=self.zero_one_loss_over_minibatch(self.y),
            givens={
                self.x: valid_set_x[index * batch_size: (index + 1) * batch_size],
                self.y: valid_set_y[index * batch_size: (index + 1) * batch_size]
            }
        )

        # compute the gradient of cost with respect to theta = (W,b)
        g_W = T.grad(cost=cost, wrt=self.W)
        g_b = T.grad(cost=cost, wrt=self.b)

        # specify how to update the parameters of the model as a list of
        # (variable, update expression) pairs.
        updates = [(self.W, self.W - learning_rate * g_W),
                   (self.b, self.b - learning_rate * g_b)]

        # compiling a Theano function `train_model` that returns the cost
        # and updates the model parameters based on `updates`
        train_model = theano.function(
            inputs=[index],
            outputs=cost,
            updates=updates,
            givens={
                self.x: train_set_x[index * batch_size: (index + 1) * batch_size],
                self.y: train_set_y[index * batch_size: (index + 1) * batch_size]
            }
        )

        print '... training the model'
        # early-stopping parameters
        patience = 5000  # look as this many examples regardless
        patience_increase = 2  # wait this much longer when a new best is
        # found
        improvement_threshold = 0.995  # a relative improvement of this much is
        # considered significant
        validation_frequency = min(n_train_batches, patience / 2)
        # go through this many
        # minibatche before checking the network
        # on the validation set; in this case we
        # check every epoch

        best_validation_loss = numpy.inf
        start_time = time.clock()

        done_looping = False
        epoch = 0
        while (epoch < n_epochs) and (not done_looping):
            epoch = epoch + 1
            for minibatch_index in xrange(n_train_batches):

                minibatch_avg_cost = train_model(minibatch_index)
                # iteration number
                iter = (epoch - 1) * n_train_batches + minibatch_index

                if (iter + 1) % validation_frequency == 0:
                    # compute zero-one loss on validation set
                    validation_losses = [validate_model(i)
                                         for i in xrange(n_valid_batches)]
                    this_validation_loss = numpy.mean(validation_losses)

                    print(
                        'epoch %i, minibatch %i/%i, validation error %f %%' %
                        (
                            epoch,
                            minibatch_index + 1,
                            n_train_batches,
                            this_validation_loss * 100.
                        )
                    )

                    # if we got the best validation score until now
                    if this_validation_loss < best_validation_loss:
                        # improve patience if loss improvement is good enough
                        if this_validation_loss < best_validation_loss * \
                                improvement_threshold:
                            patience = max(patience, iter * patience_increase)

                        best_validation_loss = this_validation_loss

                        print "epoch:" + str(epoch) + ", minibatch:" + str(minibatch_index + 1) + "/" + str(
                            n_train_batches) + ", validation loss:" + str(this_validation_loss)

                if patience <= iter:
                    done_looping = True
                    break

        end_time = time.clock()
        print "Optimization complete. Best validation score:", best_validation_loss
        print "The code run for ", epoch, "epochs, with", 1. * epoch / (end_time - start_time), "epochs/sec"

    def zero_one_loss_over_minibatch(self, y):
        # check if y has same dimension of y_predicted
        if y.ndim != self.y_predicted.ndim:
            raise TypeError(
                'y should have the same shape as self.y_pred',
                ('y', y.type, 'y_pred', self.y_predicted.type)
            )
        # check if y is of the correct datatype
        if y.dtype.startswith('int'):
            # the T.neq operator returns a vector of 0s and 1s, where 1
            # represents a mistake in prediction
            return T.mean(T.neq(self.y_predicted, y))
        else:
            raise NotImplementedError()

    def classify(self, x):
        return self.output_probabilities([x])



