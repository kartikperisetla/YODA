#!/usr/bin/env python 
import sys
import getopt
import random as r
import RecurrentNeuralNetworkModel2
import cPickle

# what fraction of the given data set should be used as validation? (approximate, sampling is random)
validation_fraction = .2

training_corpus = ''
model_file = ''

try:
    opts, args = getopt.getopt(sys.argv[1:], "ht:m:", ["train=", "model="])
except getopt.GetoptError:
    print 'train_classifier.py -t <train> -m <model>'
    sys.exit(2)

for opt, arg in opts:
    if opt == '-h':
        print 'train_classifier.py -t <train> -m <model>'
        sys.exit()
    elif opt in ("-t", "--train"):
        training_corpus = arg
    elif opt in ("-m", "--model"):
        model_file = arg

print model_file, training_corpus


# Load training data set
print "loading training data set"

n_context_features = None
n_token_features = None
n_output_labels = 0
training_samples = []
validation_samples = []

f = open(training_corpus, 'r')
for line in f:
    [features, outputs] = line.split(" -> ")
    [context_features, sequence_feature_vectors] = features.split(" : ")
    context_features = eval(context_features)
    sequence_feature_vectors = eval(sequence_feature_vectors)
    outputs = eval(outputs)

    if r.random() < validation_fraction:
        validation_samples.append([[context_features, sequence_feature_vectors], outputs])
    else:
        training_samples.append([[context_features, sequence_feature_vectors], outputs])

    if n_context_features is None:
        n_context_features = len(context_features)
    if n_token_features is None:
        n_token_features = len(sequence_feature_vectors[0])
    for output in outputs:
        n_output_labels = max(n_output_labels, output+1)
    for token_feature_vector in sequence_feature_vectors:
        n_output_labels = max(n_output_labels, output+1)

f.close()

print "number of context features:", n_context_features
print "number of token features:", n_token_features
print "number of output labels:", n_output_labels
print "number of samples in training set:", len(training_samples)
print "number of samples in validation set:", len(validation_samples)
sys.stdout.flush()

print "creating RNN2 model..."
rnn = RecurrentNeuralNetworkModel2.RecurrentNeuralNetworkModel2(
    n_context_features,
    n_token_features,
    50,
    n_output_labels,
    5)


print "training RNN2 model..."
settings = {'lr': .0627142536696559,
            'verbose': 1,
            'decay': False,  # decay on the learning rate if improvement stops
            'seed': 345,
            'n_epochs': 500}
rnn.train(zip(*training_samples), zip(*validation_samples), settings)

# print "Creating multi-classifier"
# multi_classifier = MultiClassifier.MultiClassifier(n_features, classifier_ranges)
# print "Starting training:"
# multi_classifier.train(training_samples, validation_samples)
# print "Done training multi-classifier."
#
# print "saving model file"
# f = open(model_file, 'wb')
# cPickle.dump(multi_classifier, f, protocol=cPickle.HIGHEST_PROTOCOL)
# f.close()