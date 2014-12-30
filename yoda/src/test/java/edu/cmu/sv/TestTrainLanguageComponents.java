package edu.cmu.sv;

import edu.cmu.sv.natural_language_generation.CorpusGeneration;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.NodeMultiClassificationProblem;
import edu.cmu.sv.utils.StringDistribution;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 *
 * Generate an artificial corpus and use it to train language components (SLU / LM)
 */
public class TestTrainLanguageComponents {

    @Test
    public void Test(){
        Set<String> vocabulary = new HashSet<>();
        Map<String, Set<Object>> classificationVariablesAndRanges = new HashMap<>();

        Set<NodeMultiClassificationProblem> multiClassificationProblems = new HashSet<>();
        Map<String, SemanticsModel> corpus = CorpusGeneration.generateCorpus();

        for (String utterance : corpus.keySet()){
            List<String> tokens = Tokenizer.tokenize(utterance);
            vocabulary.addAll(tokens);

            SemanticsModel semanticsModel = corpus.get(utterance);
            for (String slotPath : semanticsModel.getAllInternalNodePaths()) {
                NodeMultiClassificationProblem multiClassificationProblem =
                        new NodeMultiClassificationProblem(utterance, semanticsModel.getInternalRepresentation(), slotPath);
                Map<String, Object> classificationResults = new HashMap<>();
                JSONObject groundTruthContent = (JSONObject) semanticsModel.newGetSlotPathFiller(slotPath);
                for (Object key : groundTruthContent.keySet()){
                    if (key.equals("class") || key.equals("dialogAct"))
                        classificationResults.put((String)key, groundTruthContent.get(key));

                }

                // collect the full set of classification results and keys that appear in the data
                for (String key : classificationResults.keySet()){
                    if (!classificationVariablesAndRanges.containsKey(key))
                        classificationVariablesAndRanges.put(key, new HashSet<>());
                    classificationVariablesAndRanges.get(key).add(classificationResults.get(key));
                }

                multiClassificationProblem.outputDistribution = new StringDistribution();
                multiClassificationProblem.outputDistribution.put("ground_truth", 1.0);
                multiClassificationProblem.outputRolesAndFillers = new HashMap<>();
                multiClassificationProblem.outputRolesAndFillers.put("ground_truth", classificationResults);
                multiClassificationProblems.add(multiClassificationProblem);
            }
        }

        // each of the classification problems could have a null result
        for (String variable : classificationVariablesAndRanges.keySet()){
            classificationVariablesAndRanges.get(variable).add(null);
        }

    }
}
