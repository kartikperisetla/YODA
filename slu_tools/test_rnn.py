# Copied from https://github.com/mesnilgr/is13 : elman-forward.py : Jan 1, 2015

import numpy
import random
import RecurrentNeuralNetworkModel as RNN

settings = {'lr': .0627142536696559,
            'verbose': 1,
            'decay': False,  # decay on the learning rate if improvement stops
            'win': 3,  # number of words in the context window
            'bs': 9,  # number of backprop through time steps
            'nhidden': 3,  # number of hidden units
            'seed': 345,
            'emb_dimension': 3,  # dimension of word embedding
            'n_epochs': 500}


# data set of noun phrase tagging
data_set = [("are the people going to school", [0, 1, 1, 0, 0, 1]),
    ("where are you going", [1, 0, 1, 0]),
    ("people will talk", [1, 0, 0]),
    ("the school yard is full of people", [1,1,1,0,0,0,1]),
    ("where in the world are they", [1,0,1,1,0,1]),
    ("they are people", [1,0,1]),
    ("i want to uh go people watching", [1,0,0,0,0,0,0]),
    ("uh talk to them", [0,0,0,1]),
    ("uh i can hear you", [0,1,0,0,1]),
    ("i want to uh school you", [1,0,0,0,0,1]),
    ("i want to go people watching", [1,0,0,0,0,0]),
    ("talk to them", [0,0,1]),
    ("i can hear you", [1,0,0,1]),
    ("i want to school you", [1,0,0,0,1]),
    ("is the school open", [0,1,1,0]),
    ("world wide people say the school is good", [0,0,1,0,1,1,0,0]),
    ("around the world people think you are watching", [0,1,1,1,0,1,0,0]),
    ("the school is not open", [1,1,0,0,0]),
    ("you hear that the good school is open", [1,0,0,1,1,1,0,0]),
    ("i hear that you are people watching at the school", [1,0,0,1,0,0,0,0,1,1]),
    ("is the uh school open", [0,1,1,1,0]),
    ("world wide people say the school is uh good", [0,0,1,0,1,1,0,0,0])]

import collections as C
counter = C.Counter()

for sample in data_set:
    assert len(sample[0].split()) == len(sample[1])
    for word in sample[0].split():
        counter[word] += 1

print counter
vocab = sorted(["<unk>"] + [word for word in counter.keys() if counter[word] > 1])
indexToWord = {x:vocab[x] for x in range(len(vocab))}
wordToIndex = {vocab[x]:x for x in range(len(vocab))}
print vocab


def get_index(word):
    if word in wordToIndex:
        return wordToIndex[word]
    else:
        return wordToIndex["<unk>"]


data_set = [([get_index(j) for j in data_set[i][0].split()] , data_set[i][1]) for i in range(len(data_set))]

n_train = 14
x_train = [sample[0] for sample in data_set[0:n_train]]
x_validate = [sample[0] for sample in data_set[n_train:]]
y_train = [sample[1] for sample in data_set[0:n_train]]
y_validate = [sample[1] for sample in data_set[n_train:]]

nclasses = 2
vocsize = len(vocab)

# instantiate the model
numpy.random.seed(settings['seed'])
random.seed(settings['seed'])
rnn = RNN.RecurrentNeuralNetworkModel(nh=settings['nhidden'],
                                      nc=nclasses,
                                      ne=vocsize,
                                      de=settings['emb_dimension'],
                                      cs=settings['win'])

rnn.train((x_train, y_train), (x_validate, y_validate), settings)

def tag_utterance(utterance):
    return rnn.predict([get_index(w) for w in utterance.split()], settings)

for utterance in ["school is out", "is school out", "is the carnegie mellon school open"]:
    print utterance
    print tag_utterance(utterance)



