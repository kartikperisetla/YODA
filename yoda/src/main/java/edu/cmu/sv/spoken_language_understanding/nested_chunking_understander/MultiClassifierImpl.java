package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import java.util.*;
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

    Map<String, Integer> featurePositionMap = new HashMap<>();
    Map<String, List<Object>> outputInterpretation;

    public MultiClassifierImpl(Map<String, Integer> featurePositionMap, Map<String, List<Object>> outputInterpretation) {
        this.featurePositionMap = featurePositionMap;
        this.outputInterpretation = outputInterpretation;
    }

    public MultiClassifierImpl() {
    }

    @Override
    public void classify(NodeMultiClassificationProblem classificationProblem) {
        String theanoString = packTestSample(classificationProblem);

        //todo: run model given input

        //todo: read generated result

    }

    @Override
    public String packTestSample(NodeMultiClassificationProblem classificationProblem){
        List<Double> features = featureVector(extractFeatures(classificationProblem));
        return "[" + String.join(", ", features.stream().map(Object::toString).collect(Collectors.toList())) + "]";
    }

    @Override
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

    @Override
    public Set<String> extractFeatures(NodeMultiClassificationProblem classificationProblem) {
        Set<String> featuresPresent = new HashSet<>();

        //// collect features present
        String[] tmp = classificationProblem.stringForAnalysis.split(" ");
        List<String> tokens = new LinkedList<>(Arrays.asList(tmp));

        // collect unigram features
        for (int i = 0; i < tokens.size(); i++) {
            featuresPresent.add("Unigram: " + tokens.get(i));
        }

        tokens.add(0, "<S>");
        tokens.add("</S>");

        // collect bigram features
        for (int i = 0; i < tokens.size() - 1; i++) {
            featuresPresent.add("Bigram: " + tokens.get(i) + ", " + tokens.get(i + 1));
        }

        // collect context features
        String[] fillerPath = classificationProblem.contextPathInStructure.split("\\.");
        for (int i = 0; i < fillerPath.length; i++) {
            featuresPresent.add("NodeContext: " + (fillerPath.length - i) + ", " + fillerPath[i]);
        }
        return featuresPresent;
    }

    @Override
    public List<Double> featureVector(Set<String> featuresPresent) {
            //// assemble feature vector
        List<Double> features = new LinkedList<>();
        Set<Integer> featuresOn = new HashSet<>();
        for (String presentFeature : featuresPresent) {
            if (featurePositionMap.containsKey(presentFeature))
                featuresOn.add(featurePositionMap.get(presentFeature));
            else
                System.out.println("WARNING: present feature not in model:" + presentFeature);
        }

        for (int i = 0; i < featurePositionMap.size(); i++) {
            if (featuresOn.contains(i))
                features.add(1.0);
            else
                features.add(0.0);
        }
        return features;
    }



}
