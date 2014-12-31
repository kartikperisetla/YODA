# MultiClassifier.py
import LogisticRegressionModel as LR
import theano
import numpy as np
import theano.tensor as T


class MultiClassifier:

    def __init__(self, n_features, classifier_ranges):
        self.classifiers = dict()
        self.n_features = n_features
        self.classifier_ranges = classifier_ranges
        for key in self.classifier_ranges.keys():
            self.classifiers[key] = LR.LogisticRegressionModel(self.n_features, self.classifier_ranges[key])

    def train(self, training_set, validation_set):
        x_train = theano.shared(np.array([sample[0] for sample in training_set]))
        x_validate = theano.shared(np.array([sample[0] for sample in validation_set]))
        for key in self.classifiers.keys():
            y_train = T.cast(theano.shared(np.array([sample[1][key] for sample in training_set])), 'int32')
            y_validate = T.cast(theano.shared(np.array([sample[1][key] for sample in validation_set])), 'int32')
            print "training classifier", key
            self.classifiers[key].train(((x_train, y_train),
                                         (x_validate, y_validate)))

    def classify(self, features):
        # print "classifier keys:", self.classifiers.keys()
        return {x: self.classifiers[x].classify(features).tolist() for x in self.classifiers.keys()}

