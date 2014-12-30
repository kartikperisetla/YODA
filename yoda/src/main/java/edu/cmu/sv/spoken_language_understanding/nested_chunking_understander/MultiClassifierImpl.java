package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 12/30/14.
 */
public class MultiClassifierImpl implements MultiClassifier {

    public static class VariableNotClassified {
        @Override
        public String toString() {
            return "0NotClassified";
        }
    }
    public static final VariableNotClassified NOT_CLASSIFIED = new VariableNotClassified();

    ClassificationFeatureExtractor featureExtractor;
    Map<String, List<Object>> outputInterpretation;

    public MultiClassifierImpl(ClassificationFeatureExtractor featureExtractor, Map<String, List<Object>> outputInterpretation) {
        this.featureExtractor = featureExtractor;
        this.outputInterpretation = outputInterpretation;
    }

    @Override
    public void classify(NodeMultiClassificationProblem classificationProblem) {
        String theanoString = packTestSample(classificationProblem);

        //todo: run model given input

        //todo: read generated result

    }

    public String packTestSample(NodeMultiClassificationProblem classificationProblem){
        List<Double> features = featureExtractor.generateFeatures(classificationProblem);
        return "[" + String.join(", ", features.stream().map(Object::toString).collect(Collectors.toList())) + "]";
    }

    public String packTrainingSample(NodeMultiClassificationProblem classificationProblem){
        String ans = packTestSample(classificationProblem);
        List<Integer> output = new LinkedList<>();
        for (String variable : outputInterpretation.keySet().stream().sorted().collect(Collectors.toList())){
            output.add(outputInterpretation.get(variable).
                    indexOf(classificationProblem.outputRolesAndFillers.get("ground_truth").get(variable)));
        }
        ans += " -> ";
        ans += "[" + String.join(", ", output.stream().map(Object::toString).collect(Collectors.toList())) + "]";
        return ans;
    }



}
