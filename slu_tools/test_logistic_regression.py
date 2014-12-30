#!/usr/bin/env python
import LogisticRegressionModel as LR
import numpy as np
import theano
import theano.tensor as T
import random as r

mean_0 = 1
mean_1 = -1
st_dev = 1.5

n_train = 100000
n_validate = 10000

print "generating artificial dataset"

x_train = None
y_train = []
for i in xrange(n_train):
    if r.random() > .5:
        tmp = np.random.normal(mean_0, st_dev, (1, 2))
        if x_train is None:
            x_train = tmp
        else:
            x_train = np.concatenate((x_train, tmp), axis=0)
        y_train.append(0)
    else:
        tmp = np.random.normal(mean_1, st_dev, (1, 2))
        if x_train is None:
            x_train = tmp
        else:
            x_train = np.concatenate((x_train, tmp), axis=0)
        y_train.append(1)

y_train = np.array(y_train).astype(int)

x_validate = None
y_validate = []
for i in xrange(n_validate):
    if r.random() > .5:
        tmp = np.random.normal(mean_0, st_dev, (1, 2))
        if x_validate is None:
            x_validate = tmp
        else:
            x_validate = np.concatenate((x_validate, tmp), axis=0)
        y_validate.append(0)
    else:
        tmp = np.random.normal(mean_1, st_dev, (1, 2))
        if x_validate is None:
            x_validate = tmp
        else:
            x_validate = np.concatenate((x_validate, tmp), axis=0)
        y_validate.append(1)

y_validate = np.array(y_validate).astype(int)


# x_train = np.concatenate((np.random.normal(mean_0, st_dev, (n_train, 2)),
#                           np.random.normal(mean_1, st_dev, (n_train, 2))),
#                          axis=0)
# 
# y_train = np.array([0]*n_train + [1]*n_train).astype(int)

# x_validate = np.concatenate((np.random.normal(mean_0, st_dev, (n_validate, 2)),
#                              np.random.normal(mean_1, st_dev, (n_validate, 2))),
#                             axis=0)
#
# y_validate = np.array([0]*n_validate + [1]*n_validate).astype(int)


# print x_train.shape
# print y_train.shape

print "initializing model"
model = LR.LogisticRegressionModel(2, 2)

print "training model"
model.train(((theano.shared(x_train), T.cast(theano.shared(y_train), 'int32')),
             (theano.shared(x_validate), T.cast(theano.shared(y_validate), 'int32'))))

print "classifying a clear 0 class sample"
print model.classify(np.array([1, 1]))

print "classifying a clear 1 class sample"
print model.classify(np.array([-1, -1]))



# print np.mean(x)
# print np.std(x)