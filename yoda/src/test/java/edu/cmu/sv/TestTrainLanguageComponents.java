package edu.cmu.sv;

import com.google.common.collect.*;
import edu.cmu.sv.natural_language_generation.CorpusGeneration;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.MultiClassifier;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.NodeMultiClassificationProblem;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
    static String chunkerTrainingFile = "./src/resources/corpora/chunker_training_file.txt";
    static String serializedChunkerFile = "./src/resources/models_and_serialized_objects/serialized_chunker.srl";


    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        Multiset<String> featureCounter = HashMultiset.create();
        Set<String> vocabulary = new HashSet<>();
        HashMap<String, LinkedList<Serializable>> classificationVariablesAndRanges = new HashMap<>();

        Set<NodeMultiClassificationProblem> multiClassificationProblems = new HashSet<>();
        System.out.println("generating corpus");
        Map<String, SemanticsModel> corpus = CorpusGeneration.generateCorpus();

        System.out.println("collecting features and training samples");
        for (String utterance : corpus.keySet()){
            List<String> tokens = Tokenizer.tokenize(utterance);
            vocabulary.addAll(tokens);

            SemanticsModel semanticsModel = corpus.get(utterance);

            for (String slotPath : Iterables.concat(semanticsModel.getAllInternalNodePaths(), Arrays.asList(""))) {
                NodeMultiClassificationProblem multiClassificationProblem =
                        new NodeMultiClassificationProblem(utterance, semanticsModel.getInternalRepresentation(), slotPath);
                featureCounter.addAll(MultiClassifier.extractFeatures(multiClassificationProblem));

                Map<String, Object> classificationResults = new HashMap<>();
                JSONObject groundTruthContent = (JSONObject) semanticsModel.newGetSlotPathFiller(slotPath);
                for (Object key : groundTruthContent.keySet()){
                    if (key.equals("class") || key.equals("dialogAct"))
                        classificationResults.put((String)key, (String)groundTruthContent.get(key));

                }

                // collect the full set of classification results and keys that appear in the data
                for (String key : classificationResults.keySet()){
                    if (!classificationVariablesAndRanges.containsKey(key))
                        classificationVariablesAndRanges.put(key, new LinkedList<>(Arrays.asList(MultiClassifier.NOT_CLASSIFIED)));
                    if (!classificationVariablesAndRanges.get(key).contains(classificationResults.get(key)))
                        classificationVariablesAndRanges.get(key).add((Serializable)classificationResults.get(key));
                }

                multiClassificationProblem.outputDistribution = new StringDistribution();
                multiClassificationProblem.outputDistribution.put("ground_truth", 1.0);
                multiClassificationProblem.outputRolesAndFillers = new HashMap<>();
                multiClassificationProblem.outputRolesAndFillers.put("ground_truth", classificationResults);
                multiClassificationProblems.add(multiClassificationProblem);
            }
        }

//        System.out.println("feature counter's size:" + featureCounter.size());
//        for (String feature : featureCounter.elementSet()){
//            System.out.println("feature:    "+feature+", count:    "+featureCounter.count(feature));
//        }

        List<String> retainedFeatures = featureCounter.elementSet().stream().
                filter(x -> featureCounter.count(x) > 1).collect(Collectors.toList());
        HashMap<String, Integer> featurePositionMap = new HashMap<>();
        for (int i = 0; i < retainedFeatures.size(); i++) {
            featurePositionMap.put(retainedFeatures.get(i), i);
        }

        System.out.println("classification variables and ranges:\n"+classificationVariablesAndRanges);

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(MultiClassifier.serializedClassifierPreferencesFile));
            out.writeObject(new ArrayList<Serializable>(Arrays.asList((Serializable) featurePositionMap, (Serializable) classificationVariablesAndRanges)));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //// write out multi-classifier training file
        MultiClassifier.loadPreferences();
        System.out.println("writing multi-classifier training file...");
        PrintWriter writer = new PrintWriter(MultiClassifier.classifierTrainingFile, "UTF-8");
        for (Object classificationProblem : multiClassificationProblems){
            writer.write(MultiClassifier.packTrainingSample((NodeMultiClassificationProblem) classificationProblem)+"\n");
        }
        writer.close();

        // train model
        System.out.println("training theano model ...");
        MultiClassifier.trainTheanoModel();
        System.out.println("done training theano model.");
    }
}
