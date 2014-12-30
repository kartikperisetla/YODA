package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import java.util.List;
import java.util.Set;

/**
 * Created by David Cohen on 12/29/14.
 */
public interface MultiClassifier {
    public void classify(NodeMultiClassificationProblem classificationProblem);
    public Set<String> extractFeatures(NodeMultiClassificationProblem classificationProblem);
    public List<Double> featureVector(Set<String> featuresPresent);
    public String packTestSample(NodeMultiClassificationProblem classificationProblem);
    public String packTrainingSample(NodeMultiClassificationProblem classificationProblem);

}
