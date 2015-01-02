package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by David Cohen on 12/29/14.
 */
public class Chunker {
    static Map<String, Integer> contextFeaturePositionMap = new HashMap<>();

    public Pair<List<Double>, List<List<String>>> generateFeatures(ChunkingProblem chunkingProblem) {

        //// collect context features
        Set<String> featuresPresent = new HashSet();
        String[] fillerPath = chunkingProblem.contextPathInStructure.split("\\.");
        for (int i = 0; i < fillerPath.length; i++) {
            featuresPresent.add("NodeContext: " + (fillerPath.length - i) + ", " + fillerPath[i]);
        }

        //// assemble context feature vector
        List<Double> contextFeatures = new LinkedList<>();
        for (int i = 0; i < contextFeaturePositionMap.size(); i++) {
            for (String presentFeature : featuresPresent) {
                if (contextFeaturePositionMap.containsKey(presentFeature) && contextFeaturePositionMap.get(presentFeature).equals(i)){
                    contextFeatures.add(1.0);
                } else if (!contextFeaturePositionMap.containsKey(presentFeature)) {
                    System.out.println("WARNING: present feature not in model: "+presentFeature);
                } else {
                    contextFeatures.add(0.0);
                }
            }
        }

        //// collect token feature vectors
        List<List<String>> tokenFeatures = new LinkedList<>();
        String[] tokens = chunkingProblem.stringForAnalysis.split(" ");
        for (int i = 0; i < tokens.length; i++) {
            // the word is the only feature
            tokenFeatures.add(new LinkedList<>(Arrays.asList(tokens[i])));
        }

        return new ImmutablePair<>(contextFeatures, tokenFeatures);
    }



    public void chunk(ChunkingProblem chunkingProblem) {
        Pair<List<Double>, List<List<String>>> features = generateFeatures(chunkingProblem);

        //todo: pack features as raw input for theano model

        //todo: run model given input

        //todo: read generated result

        //todo:
    }
}
