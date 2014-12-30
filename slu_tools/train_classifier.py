#!/usr/bin/env python 
import sys
import getopt
import MultiClassifier

training_corpus = ''
model_file = ''

try:
    opts, args = getopt.getopt(sys.argv[1:],"ht:m:",["train=","model="])
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

model = MultiClassifier.MultiClassifier()


# Load training data set
f = open(training_corpus, 'r')
for line in f:

