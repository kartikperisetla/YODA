package edu.cmu.sv;

import com.google.common.collect.*;
import edu.cmu.sv.natural_language_generation.CorpusGeneration;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.Chunker;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.ChunkingProblem;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.MultiClassifier;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.NodeMultiClassificationProblem;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 10/29/14.
 *
 * Generate an artificial corpus and use it to train language components (SLU / LM)
 */
public class TestTrainLanguageComponents {


    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        Multiset<String> featureCounter = HashMultiset.create();
        List<Multiset<String>> tokenFeatureCounter = new LinkedList<>();
        LinkedList<String> chunkingOutputLabels = new LinkedList<>(Arrays.asList(Chunker.NO_LABEL));
        LinkedList<String> chunkingContextFeatures = new LinkedList<>();
        HashMap<String, LinkedList<String>> classificationVariablesAndRanges = new HashMap<>();

        Set<ChunkingProblem> chunkingProblems = new HashSet<>();
        Set<NodeMultiClassificationProblem> multiClassificationProblems = new HashSet<>();
        System.out.println("generating corpus");
        Map<String, SemanticsModel> corpus = CorpusGeneration.generateCorpus();

        System.out.println("collecting features and training samples");
        for (String utterance : corpus.keySet()) {

            SemanticsModel semanticsModel = corpus.get(utterance);

            for (String slotPath : Iterables.concat(semanticsModel.getAllInternalNodePaths(), Arrays.asList(""))) {
                JSONObject groundTruthContent = (JSONObject) semanticsModel.newGetSlotPathFiller(slotPath);

                //// build classification problem at this node
                NodeMultiClassificationProblem multiClassificationProblem =
                        new NodeMultiClassificationProblem(utterance, semanticsModel.getInternalRepresentation(), slotPath);
                featureCounter.addAll(MultiClassifier.extractFeatures(multiClassificationProblem));

                Map<String, Object> classificationResults = new HashMap<>();
                for (Object key : groundTruthContent.keySet()) {
                    if (key.equals("class") || key.equals("dialogAct"))
                        classificationResults.put((String) key, groundTruthContent.get(key));

                }

                // collect the full set of classification results and keys that appear in the data
                for (String key : classificationResults.keySet()) {
                    if (!classificationVariablesAndRanges.containsKey(key))
                        classificationVariablesAndRanges.put(key, new LinkedList<>(Arrays.asList(MultiClassifier.NOT_CLASSIFIED)));
                    if (!classificationVariablesAndRanges.get(key).contains(classificationResults.get(key)))
                        classificationVariablesAndRanges.get(key).add((String) classificationResults.get(key));
                }

                multiClassificationProblem.outputDistribution = new StringDistribution();
                multiClassificationProblem.outputDistribution.put("ground_truth", 1.0);
                multiClassificationProblem.outputRolesAndFillers = new HashMap<>();
                multiClassificationProblem.outputRolesAndFillers.put("ground_truth", classificationResults);
                multiClassificationProblems.add(multiClassificationProblem);

                //// build chunking problem at this node
                if (slotPath.equals("") ||
                        (groundTruthContent.containsKey("chunk-start") && groundTruthContent.containsKey("chunk-end"))) {

                    Pair<Integer, Integer> chunkingIndices = SemanticsModel.getChunkingIndices(groundTruthContent);
                    ChunkingProblem chunkingProblem = new ChunkingProblem(
                            utterance,
                            corpus.get(utterance).getInternalRepresentation(),
                            slotPath,
                            chunkingIndices);

                    List<List<String>> sequenceFeatures = Chunker.extractSequenceFeatures(chunkingProblem);
                    for (List<String> tokenFeatures : sequenceFeatures) {
                        for (int j = 0; j < tokenFeatures.size(); j++) {
                            if (tokenFeatureCounter.size() < j + 1)
                                tokenFeatureCounter.add(HashMultiset.create());
                            tokenFeatureCounter.get(j).add(tokenFeatures.get(j));
                        }
                    }
                    Chunker.extractContextFeatures(chunkingProblem).stream().
                            filter(contextFeature -> !chunkingContextFeatures.contains(contextFeature)).
                            forEach(chunkingContextFeatures::add);

                    Set<ChunkingProblem> childChunkingProblems = new HashSet<>();

                    // extract child chunking problems by recursively searching for descendants with chunking indices
                    Set<Pair<String, JSONObject>> activeChildren = new HashSet<>();

                    for (Object key : groundTruthContent.keySet()) {
                        if (groundTruthContent.get(key) instanceof JSONObject) {
                            activeChildren.add(new ImmutablePair<>(
                                    (String) key,
                                    (JSONObject) groundTruthContent.get(key)));
                        }
                    }

                    while (!activeChildren.isEmpty()) {
                        Iterator<Pair<String, JSONObject>> it = activeChildren.iterator();
                        Pair<String, JSONObject> child = it.next();
                        it.remove();
                        if (child.getValue().containsKey("chunk-start")) {
                            if (!chunkingOutputLabels.contains(child.getLeft())) {
                                chunkingOutputLabels.add(child.getLeft() + "-B");
                                chunkingOutputLabels.add(child.getLeft() + "-I");
                            }

                            Pair<Integer, Integer> childChunkingIndices = SemanticsModel.getChunkingIndices(child.getRight());
                            String childSlotPath = child.getLeft();
                            if (!slotPath.equals(""))
                                childSlotPath = slotPath + "." + childSlotPath;

                            ChunkingProblem childChunkingProblem = new ChunkingProblem(
                                    utterance,
                                    corpus.get(utterance).getInternalRepresentation(),
                                    childSlotPath,
                                    childChunkingIndices);
                            childChunkingProblems.add(childChunkingProblem);
                        } else {
                            for (Object key : child.getRight().keySet()) {
                                if (child.getRight().get(key) instanceof JSONObject) {
                                    activeChildren.add(new ImmutablePair<>(
                                            child.getLeft() + "." + key,
                                            (JSONObject) child.getRight().get(key)));
                                }
                            }
                        }
                    }

                    chunkingProblem.outputDistribution = new StringDistribution();
                    chunkingProblem.outputDistribution.put("ground_truth", 1.0);
                    chunkingProblem.outputChildChunkingProblems = new HashMap<>();
                    chunkingProblem.outputChildChunkingProblems.put("ground_truth", childChunkingProblems);
                    chunkingProblems.add(chunkingProblem);
                }
            }
        }

//        System.out.println("feature counter's size:" + featureCounter.size());
//        for (String feature : featureCounter.elementSet()){
//            System.out.println("feature:    "+feature+", count:    "+featureCounter.count(feature));
//        }

        //// write out chunker preferences and training file
        {
            // select chunking features from those that were seen
            LinkedList<LinkedList<String>> tokenFeatures = new LinkedList<>();
            for (Multiset<String> counter : tokenFeatureCounter) {
                LinkedList retainedFeatures = new LinkedList(counter.elementSet().stream().
                        filter(x -> counter.count(x) > 1).collect(Collectors.toList()));
                retainedFeatures.add(0, Chunker.UNK);
                tokenFeatures.add(retainedFeatures);
            }
            // retain all context features and output labels

            // write out chunking preferences
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Chunker.serializedChunkerPreferencesFile));
                out.writeObject(new ArrayList<>(Arrays.asList(
                        chunkingContextFeatures,
                        (Serializable) tokenFeatures,
                        chunkingOutputLabels)));
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // write out training file
            Chunker.loadPreferences();
            System.out.println("writing chunker training file...");
            PrintWriter writer = new PrintWriter(Chunker.chunkerTrainingFile, "UTF-8");
            for (ChunkingProblem problem : chunkingProblems) {
                writer.write(Chunker.packTrainingSample(problem) + "\n");
            }
            writer.close();
        }

        // train model
        System.out.println("training theano chunker model ...");
        Chunker.trainTheanoModel();
        System.out.println("done training chunker model.");



        System.exit(0);

        //// write out multi-classifier preferences and training file
        {
            // select classification features from those that were generated
            List<String> retainedFeatures = featureCounter.elementSet().stream().
                    filter(x -> featureCounter.count(x) > 1).collect(Collectors.toList());
            HashMap<String, Integer> featurePositionMap = new HashMap<>();
            for (int i = 0; i < retainedFeatures.size(); i++) {
                featurePositionMap.put(retainedFeatures.get(i), i);
            }
            System.out.println("classification variables and ranges:\n" + classificationVariablesAndRanges);

            // write out classification preferences
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(MultiClassifier.serializedClassifierPreferencesFile));
                out.writeObject(new ArrayList<>(Arrays.asList(featurePositionMap, (Serializable) classificationVariablesAndRanges)));
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // write out multi-classifier training file
            MultiClassifier.loadPreferences();
            System.out.println("writing multi-classifier training file...");
            PrintWriter writer = new PrintWriter(MultiClassifier.classifierTrainingFile, "UTF-8");
            for (Object classificationProblem : multiClassificationProblems) {
                writer.write(MultiClassifier.packTrainingSample((NodeMultiClassificationProblem) classificationProblem) + "\n");
            }
            writer.close();
        }

        // train model
        System.out.println("training theano classifier model ...");
        MultiClassifier.trainTheanoModel();
        System.out.println("done training classifier model.");
    }
}
