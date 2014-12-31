#!/usr/bin/env python 
import sys
import getopt
import MultiClassifier
import random as r

# what fraction of the given data set should be used as validation? (approximate, sampling is random)
validation_fraction = .2

training_corpus = ''
model_file = ''

try:
    opts, args = getopt.getopt(sys.argv[1:], "ht:m:", ["train=", "model="])
except getopt.GetoptError:
    print 'test.py -t <train> -m <model>'
    sys.exit(2)

for opt, arg in opts:
    if opt == '-h':
        print 'test.py -t <train> -m <model>'
        sys.exit()
    elif opt in ("-t", "--train"):
        training_corpus = arg
    elif opt in ("-m", "--model"):
        model_file = arg

print model_file, training_corpus


# Load training data set
print "loading training data set"

n_features = None
classifier_ranges = dict()
n_classifiers = None
training_samples = []
validation_samples = []

f = open(training_corpus, 'r')
for line in f:
    [features, outputs] = line.split(" -> ")
    features = eval(features)
    outputs = eval(outputs)

    if r.random() < validation_fraction:
        validation_samples.append([features, outputs])
    else:
        training_samples.append([features, outputs])

    if n_classifiers is None:
        n_classifiers = len(outputs)
        n_features = len(features)
    for (i, val) in enumerate(outputs):
        if i not in classifier_ranges:
            classifier_ranges[i] = 0
        classifier_ranges[i] = max(classifier_ranges[i], val+1)

print "n_features:", n_features
print "n_classifiers:", n_classifiers
print "classifier ranges:", classifier_ranges
print "number of samples in training set:", len(training_samples)
print "number of samples in validation set:", len(validation_samples)

print "Creating multi-classifier"
multi_classifier = MultiClassifier.MultiClassifier(n_features, classifier_ranges)
print "Starting training:"
multi_classifier.train(training_samples, validation_samples)
print "Done training multi-classifier."

