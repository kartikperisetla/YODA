# MultiClassifier.py
import LogisticRegressionModel


class MultiClassifier:
    classifiers = dict()  # map variable -> LogisticRegressionModel for that variable

    def __init__(self):
        pass

    def classify(self, features):
        return {x: self.classifiers[x].classify(features) for x in self.classifiers.keys()}

