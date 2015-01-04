package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 12/29/14.
 */
public class Chunker {
    public static final String chunkerModelFile = "src/resources/corpora/chunker.model";
    public static final String chunkerTrainingFile = "src/resources/corpora/chunker_training_file.txt";
    public static final String serializedChunkerPreferencesFile = "src/resources/models_and_serialized_objects/serialized_chunker_preferences.srl";

    public static final String NO_LABEL = "~~<<< TOKEN HAS NO LABEL >>>~~";

    static LinkedList<String> contextFeatures = new LinkedList<>();
    static LinkedList<LinkedList<String>> tokenFeatures = new LinkedList<>();
    static LinkedList<String> outputLabels = new LinkedList<>();


    public static void loadPreferences(){
        try {
            FileInputStream fileInputStream = new FileInputStream(serializedChunkerPreferencesFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            List<Object> preferences = (List<Object>) objectInputStream.readObject();
            contextFeatures = (LinkedList<String>) preferences.get(0);
            tokenFeatures = (LinkedList<LinkedList<String>>) preferences.get(1);
            outputLabels = (LinkedList<String>) preferences.get(2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> extractContextFeatures(ChunkingProblem chunkingProblem){
        //// collect context features
        Set<String> featuresPresent = new HashSet();
        String[] fillerPath = chunkingProblem.contextPathInStructure.split("\\.");
        for (int i = 0; i < fillerPath.length; i++) {
            featuresPresent.add("NodeContext: " + (fillerPath.length - i) + ", " + fillerPath[i]);
        }
        return featuresPresent;
    }

    public static List<Double> contextFeatureVector(Set<String> contextFeaturesPresent){
        //// assemble context feature vectors
        List<Double> ans= new LinkedList<>();
        for (int i = 0; i < contextFeatures.size(); i++) {
            if (contextFeaturesPresent.contains(contextFeatures.get(i)))
                ans.add(1.0);
            else
                ans.add(0.0);
        }
        contextFeaturesPresent.removeAll(contextFeatures);
        if (contextFeaturesPresent.size()>0)
            System.out.println("WARNING: present context feature(s) not in model: "+contextFeaturesPresent);
        return ans;
    }

    public static List<List<String>> extractSequenceFeatures(ChunkingProblem chunkingProblem){
        //// collect sequential feature vectors
        List<List<String>> sequenceFeatures = new LinkedList<>();
        List<String> tokens = Tokenizer.tokenize(chunkingProblem.stringForAnalysis);
        for (int i = 0; i < tokens.size(); i++) {
            List<String> tokenFeatures = new LinkedList<>();
            // the word itself is the only feature
            tokenFeatures.add(tokens.get(i));
            sequenceFeatures.add(tokenFeatures);
        }
        return sequenceFeatures;
    }

    public static List<List<Double>> sequenceFeatureVectors(List<List<String>> sequenceFeaturesPresent){
        List<List<Double>> featureVectors = new LinkedList<>();
        for (List<String> tokenFeatures : sequenceFeaturesPresent){
            List<Double> tokenFeatureVector = new LinkedList<>();
            for (int i = 0; i < tokenFeatures.size(); i++) {
                tokenFeatureVector.add((double) Chunker.tokenFeatures.get(i).indexOf(tokenFeatures.get(i)));
            }
            featureVectors.add(tokenFeatureVector);
        }
        return featureVectors;
    }

    public static String packTestSample(ChunkingProblem chunkingProblem){
        List<Double> contextFeatureVector = contextFeatureVector(extractContextFeatures(chunkingProblem));
        List<List<Double>> sequenceFeatureVectors = sequenceFeatureVectors(extractSequenceFeatures(chunkingProblem));
        String ans = "";
        // context features
        ans += "[" + contextFeatureVector.stream().map(Object::toString).collect(Collectors.toList()) + "] : ";

        // sequence features
        List<String> packedTokenStrings = new LinkedList<>();
        for (List<Double> tokenFeatureList : sequenceFeatureVectors){
            packedTokenStrings.add("[" + String.join(", ", tokenFeatureList.stream().map(Object::toString).collect(Collectors.toList())) + "]");
        }
        ans += "[" + String.join(", ", packedTokenStrings) + "]";
        return ans;
    }

    public static String packTrainingSample(ChunkingProblem chunkingProblem){
        String ans = packTestSample(chunkingProblem) + " -> ";

        List<Integer> taggingOutput = new LinkedList<>();
        List<String> tokens = Tokenizer.tokenize(chunkingProblem.stringForAnalysis);
        for (int i = 0; i < tokens.size(); i++) {
            String label = NO_LABEL;
            for (ChunkingProblem childProblem : chunkingProblem.outputChildChunkingProblems.get("ground_truth")){
                String childLabel;
                if (chunkingProblem.contextPathInStructure.equals(""))
                    childLabel = childProblem.contextPathInStructure;
                else {
                    // NOTE / WARNING: we really want to replaceFirst, but that takes a regex as input, which is hard to encode.
                    // this shouldn't be a problem, since the child path would have to
                    // more than duplicate the existing parent path for this to actually cause multiple replacements
                    childLabel = childProblem.contextPathInStructure.replace(chunkingProblem.contextPathInStructure + ".", "");
                }
                if (i==childProblem.chunkingIndices.getLeft()) {
                    label = childLabel + "-B";
                } else if (i>childProblem.chunkingIndices.getLeft() && i <=childProblem.chunkingIndices.getRight())
                    label = childLabel + "-I";
            }

            if (!outputLabels.contains(label))
                throw new Error("output label not in output interpretation! Unable to pack this training sample:"+label);
            taggingOutput.add(outputLabels.indexOf(label));
        }

        ans += "[" + String.join(", ", taggingOutput.stream().map(Object::toString).collect(Collectors.toList())) + "]";
        return ans;
    }

    public void chunk(ChunkingProblem chunkingProblem) {
        String theanoString = packTestSample(chunkingProblem)+"\n";

        //todo: run model given input
        List<Integer> taggingResult = new LinkedList<>();
        //todo: read generated result to tagging result

        List<String> labelledResult = taggingResult.stream().map(x -> outputLabels.get(x)).collect(Collectors.toList());

        Set<ChunkingProblem> childChunkingProblems = new HashSet<>();
        ChunkingProblem activeChild = null;
        Integer currentStartingIndex = null;
        String currentChunkLabel = null;
        for (int i = 0; i < labelledResult.size(); i++) {
            if (labelledResult.get(i).equals(NO_LABEL)){
                if (activeChild!=null){
                    activeChild.chunkingIndices = new ImmutablePair<>(currentStartingIndex, i-1);
                    activeChild = null;
                }
            } else {
                String chunkLabel = labelledResult.get(i).substring(0, labelledResult.get(i).length()-2);
                if (labelledResult.get(i).endsWith("-B") ||
                        (labelledResult.get(i).endsWith("-I") && (activeChild==null || !chunkLabel.equals(currentChunkLabel)))) {
                    if (activeChild != null) {
                        activeChild.chunkingIndices = new ImmutablePair<>(currentStartingIndex, i - 1);
                    }
                    currentStartingIndex = i;
                    String childContextPath = chunkLabel;
                    if (!chunkingProblem.contextPathInStructure.equals(""))
                        childContextPath = chunkingProblem.contextPathInStructure + "." + childContextPath;
                    activeChild = new ChunkingProblem(
                            chunkingProblem.fullUtterance,
                            chunkingProblem.surroundingStructure,
                            childContextPath,
                            new ImmutablePair<>(0, 0));
                    childChunkingProblems.add(activeChild);
                }
                currentChunkLabel = chunkLabel;
            }
        }
        if (activeChild!=null)
            activeChild.chunkingIndices = new ImmutablePair<>(currentStartingIndex, labelledResult.size()-1);

        String hypothesisID = "chunkingHyp0";
        StringDistribution outputDistribution = new StringDistribution();
        outputDistribution.put(hypothesisID, 1.0);
        Map<String, Set<ChunkingProblem>> outputChildChunkingProblems = new HashMap<>();
        outputChildChunkingProblems.put(hypothesisID, childChunkingProblems);
        chunkingProblem.outputDistribution = outputDistribution;
        chunkingProblem.outputChildChunkingProblems = outputChildChunkingProblems;
    }
}
