package edu.cmu.sv;

import com.google.common.collect.*;
import edu.cmu.sv.natural_language_generation.CorpusGeneration;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
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
import java.util.stream.IntStream;

/**
 * Created by David Cohen on 10/29/14.
 *
 * Generate an artificial corpus and use it to train language components (SLU / LM)
 */
public class TestTrainLanguageComponents {
    static String classifierTrainingFile = "./src/resources/classifier_training_file.txt";
    static String chunkerTrainingFile = "./src/resources/chunker_training_file.txt";

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
        classifier = new MultiClassifierImpl(featurePositionMap, classificationVariablesAndRanges);

        System.out.println("classification variables and ranges:\n"+classificationVariablesAndRanges);

        System.out.println("is red rock expensive:");
        NodeMultiClassificationProblem problem = new NodeMultiClassificationProblem("is red rock expensive", SemanticsModel.parseJSON("{}"), "");
        System.out.println(classifier.extractFeatures(problem));
        System.out.println(classifier.packTestSample(problem));



        //// write out multi-classifier training file
        System.out.println("writing multi-classifier training file...");
        PrintWriter writer = new PrintWriter(classifierTrainingFile, "UTF-8");
        for (Object classificationProblem : multiClassificationProblems){
            writer.write(classifier.packTrainingSample((NodeMultiClassificationProblem) classificationProblem)+"\n");
        }
        writer.close();



    }
}
