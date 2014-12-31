#!/usr/bin/env python 
import sys
import getopt
import MultiClassifier
import pickle

model_file = ''

try:
    opts, args = getopt.getopt(sys.argv[1:], "hm:", ["model="])
except getopt.GetoptError:
    print 'run_classifier.py -m <model>'
    sys.exit(2)

for opt, arg in opts:
    if opt == '-h':
        print 'test.py -m <model>'
        sys.exit()
    elif opt in ("-m", "--model"):
        model_file = arg

f = open(model_file, 'rb')
multi_classifier = pickle.load(f)
f.close()


sys.stdout.write("ready...\n")
sys.stdout.flush()

while True:
    line = sys.stdin.readline()
    features = eval(line)
    sys.stdout.write(str(multi_classifier.classify(features))+"\n")
    sys.stdout.flush()