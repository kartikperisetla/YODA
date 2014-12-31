package edu.cmu.sv;

import com.google.common.collect.*;
import edu.cmu.sv.natural_language_generation.CorpusGeneration;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.MultiClassifier;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.MultiClassifierImpl;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.NodeMultiClassificationProblem;
import edu.cmu.sv.utils.StringDistribution;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
        Map<String, List<Object>> classificationVariablesAndRanges = new HashMap<>();
        MultiClassifier classifier = new MultiClassifierImpl();


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
                featureCounter.addAll(classifier.extractFeatures(multiClassificationProblem));

                Map<String, Object> classificationResults = new HashMap<>();
                JSONObject groundTruthContent = (JSONObject) semanticsModel.newGetSlotPathFiller(slotPath);
                for (Object key : groundTruthContent.keySet()){
                    if (key.equals("class") || key.equals("dialogAct"))
                        classificationResults.put((String)key, groundTruthContent.get(key));

                }

                // collect the full set of classification results and keys that appear in the data
                for (String key : classificationResults.keySet()){
                    if (!classificationVariablesAndRanges.containsKey(key))
                        classificationVariablesAndRanges.put(key, new LinkedList<>(Arrays.asList(MultiClassifierImpl.NOT_CLASSIFIED)));
                    if (!classificationVariablesAndRanges.get(key).contains(classificationResults.get(key)))
                        classificationVariablesAndRanges.get(key).add(classificationResults.get(key));
                }

                multiClassificationProblem.outputDistribution = new StringDistribution();
                multiClassificationProblem.outputDistribution.put("ground_truth", 1.0);
                multiClassificationProblem.outputRolesAndFillers = new HashMap<>();
                multiClassificationProblem.outputRolesAndFillers.put("ground_truth", classificationResults);
                multiClassificationProblems.add(multiClassificationProblem);
            }
        }

        System.out.println("feature counter's size:" + featureCounter.size());
        for (String feature : featureCounter.elementSet()){
            System.out.println("feature:    "+feature+", count:    "+featureCounter.count(feature));
        }

        List<String> retainedFeatures = featureCounter.elementSet().stream().
                filter(x -> featureCounter.count(x) > 1).collect(Collectors.toList());
        Map<String, Integer> featurePositionMap = new HashMap<>();
        for (int i = 0; i < retainedFeatures.size(); i++) {
            featurePositionMap.put(retainedFeatures.get(i), i);
        }
        classifier = new MultiClassifierImpl(featurePositionMap, classificationVariablesAndRanges, false);

        System.out.println("classification variables and ranges:\n"+classificationVariablesAndRanges);

        //// write out multi-classifier training file
        System.out.println("writing multi-classifier training file...");
        PrintWriter writer = new PrintWriter(MultiClassifierImpl.classifierTrainingFile, "UTF-8");
        for (Object classificationProblem : multiClassificationProblems){
            writer.write(classifier.packTrainingSample((NodeMultiClassificationProblem) classificationProblem)+"\n");
        }
        writer.close();


        // train model
        System.out.println("training theano model ...");
        MultiClassifierImpl.trainTheanoModel();
        System.out.println("done training theano model.");


        classifier = new MultiClassifierImpl(featurePositionMap, classificationVariablesAndRanges, true);
        System.out.println("is red rock expensive:");
        NodeMultiClassificationProblem problem = new NodeMultiClassificationProblem("is red rock expensive", SemanticsModel.parseJSON("{}"), "");
        System.out.println("classifying problem...");
        classifier.classify(problem);
        classifier.classify(problem);
        classifier.classify(problem);
        classifier.classify(problem);

    }
}
