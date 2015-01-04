package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 12/29/14.
 */
public class Chunker {
    public static final String NO_LABEL = "~~<<< TOKEN HAS NO LABEL >>>~~";


    static Map<String, Integer> contextFeaturePositionMap = new HashMap<>();
    static List<String> vocabulary = new LinkedList<>();
    static List<String> outputLabels = new LinkedList<>();

    public Pair<List<Double>, List<List<Double>>> generateFeatures(ChunkingProblem chunkingProblem) {

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

        //// collect sequential feature vectors
        List<List<Double>> sequenceFeatures = new LinkedList<>();
        List<String> tokens = Tokenizer.tokenize(chunkingProblem.stringForAnalysis);
        for (int i = 0; i < tokens.size(); i++) {
            List<Double> tokenFeatures = new LinkedList<>();
            tokenFeatures.add((double)vocabulary.indexOf(tokens.get(i)));
            // the word is the only feature
            sequenceFeatures.add(tokenFeatures);
        }

        return new ImmutablePair<>(contextFeatures, sequenceFeatures);
    }

    public String packTestSample(ChunkingProblem chunkingProblem){
        Pair<List<Double>, List<List<Double>>> features = generateFeatures(chunkingProblem);
        String ans = "";
        // context features
        ans += "[" + features.getLeft().stream().map(Object::toString).collect(Collectors.toList()) + "] : ";

        // sequence features
        List<String> packedTokenStrings = new LinkedList<>();
        for (List<Double> tokenFeatureList : features.getRight()){
            packedTokenStrings.add("[" + String.join(", ", tokenFeatureList.stream().map(Object::toString).collect(Collectors.toList())) + "]");
        }
        ans += "[" + String.join(", ", packedTokenStrings) + "]";
        return ans;
    }

    public String packTrainingSample(ChunkingProblem chunkingProblem){
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
                throw new Error("output label not in output interpretation! Unable to pack this training sample");
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
