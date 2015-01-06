#!/usr/bin/env python 
import sys
import getopt
import RecurrentNeuralNetworkModel2
import MultiClassifier
import pickle

chunker_model_file = ''
multiclassifier_model_file = ''

try:
    opts, args = getopt.getopt(sys.argv[1:], "hk:c:", ["chunker-model=", "multiclassifier-model="])
except getopt.GetoptError:
    print 'run_model_server.py -k <chunker-model> -c <multiclassifier-model>'
    sys.exit(2)

for opt, arg in opts:
    if opt == '-h':
        print 'run_model_server.py -k <chunker-model> -c <multiclassifier-model>'
        sys.exit()
    elif opt in ("-k", "--chunker-model"):
        chunker_model_file = arg
    elif opt in ("-c", "--multiclassifier-model"):
        multiclassifier_model_file = arg

f = open(chunker_model_file, 'rb')
chunker = pickle.load(f)
f.close()

f = open(multiclassifier_model_file, 'rb')
multiclassifier = pickle.load(f)
f.close()

sys.stdout.write("ready...\n")
sys.stdout.flush()

while True:
    line = sys.stdin.readline()
    [model, features] = line.split("%")
    if model == "chunker":
        [context_features, sequence_feature_vectors] = features.split(" : ")
        context_features = eval(context_features)
        sequence_feature_vectors = eval(sequence_feature_vectors)
        sys.stdout.write(str(chunker.predict(context_features, sequence_feature_vectors).tolist())+"\n")
    elif model == "multiclassifier":
        utterance_features = eval(features)
        sys.stdout.write(str(multiclassifier.classify(utterance_features))+"\n")
    else:
        raise ValueError("requested model doesn't exist:" + str(model))
    sys.stdout.flush()

