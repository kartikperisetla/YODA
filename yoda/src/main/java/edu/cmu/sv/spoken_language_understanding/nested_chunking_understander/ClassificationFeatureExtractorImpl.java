package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import java.util.*;

/**
 * Created by David Cohen on 12/29/14.
 */
public class ClassificationFeatureExtractorImpl implements ClassificationFeatureExtractor {
    static Map<String, Integer> featurePositionMap = new HashMap<>();

    @Override
    public List<Double> generateFeatures(NodeMultiClassificationProblem classificationProblem) {

        Set<String> featuresPresent = new HashSet<>();

        //// collect features present
        String[] tmp = classificationProblem.stringForAnalysis.split(" ");
        List<String> tokens = new LinkedList<>(Arrays.asList(tmp));

        // collect unigram features
        for (int i = 0; i < tokens.size(); i++) {
            featuresPresent.add("Unigram: "+tokens.get(i));
        }

        tokens.add(0, "<S>");
        tokens.add("</S>");

        // collect bigram features
        for (int i = 0; i < tokens.size() - 1; i++) {
            featuresPresent.add("Bigram: "+tokens.get(i)+", "+tokens.get(i+1));
        }

        // collect context features
        String[] fillerPath = classificationProblem.contextPathInStructure.split("\\.");
        for (int i = 0; i < fillerPath.length; i++) {
            featuresPresent.add("NodeContext: " + (fillerPath.length - i) + ", " + fillerPath[i]);
        }

        //// assemble feature vector
        List<Double> features = new LinkedList<>();
        for (int i = 0; i < featurePositionMap.size(); i++) {
            for (String presentFeature : featuresPresent) {
                if (featurePositionMap.containsKey(presentFeature) && featurePositionMap.get(presentFeature).equals(i)){
                    features.add(1.0);
                } else if (!featurePositionMap.containsKey(presentFeature)) {
                    System.out.println("WARNING: present feature not in model: "+presentFeature);
                } else {
                    features.add(0.0);
                }
            }
        }
        return features;
    }
}
