#!/usr/bin/env python 
import sys
import getopt
import RecurrentNeuralNetworkModel2
import pickle

model_file = ''

try:
    opts, args = getopt.getopt(sys.argv[1:], "hm:", ["model="])
except getopt.GetoptError:
    print 'run_chunker.py -m <model>'
    sys.exit(2)

for opt, arg in opts:
    if opt == '-h':
        print 'test.py -m <model>'
        sys.exit()
    elif opt in ("-m", "--model"):
        model_file = arg

f = open(model_file, 'rb')
chunker = pickle.load(f)
f.close()


sys.stdout.write("ready...\n")
sys.stdout.flush()

while True:
    line = sys.stdin.readline()
    [context_features, sequence_feature_vectors] = line.split(" : ")
    context_features = eval(context_features)
    sequence_feature_vectors = eval(sequence_feature_vectors)
    sys.stdout.write(str(chunker.predict(context_features, sequence_feature_vectors).tolist())+"\n")
    sys.stdout.flush()