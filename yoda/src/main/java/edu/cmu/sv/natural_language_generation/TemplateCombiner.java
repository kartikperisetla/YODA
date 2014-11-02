package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Combination;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Created by David Cohen on 10/30/14.
 *
 * TemplateCombiner contains convenience functions for creating combinations of templates
 *
 */
public class TemplateCombiner {

    /*
    * Return all combinations of strings and composed semantics objects,
    * maintaining the order given as input
    * */
    public static Map<String, JSONObject> simpleOrderedCombinations(
            List<Map<String, JSONObject>> chunks,
            Function<List<JSONObject>, JSONObject> compositionFunction,
            Map<String, Pair<Integer, Integer>> childNodeChunks){
        Map<String, JSONObject> ans = new HashMap<>();

        Map<Integer, Set<Map.Entry<String, JSONObject>>> possibleBindingsInput = new HashMap<>();
        IntStream.range(0, chunks.size()).forEach(x -> possibleBindingsInput.put(x, chunks.get(x).entrySet()));

        for (Map<Integer, Map.Entry<String, JSONObject>> binding : Combination.possibleBindings(possibleBindingsInput)){
            List<String> subStrings = new LinkedList<>();
            List<JSONObject> subContents = new LinkedList<>();
            for (int i = 0; i < chunks.size(); i++) {
                subStrings.add(binding.get(i).getKey());
                subContents.add(binding.get(i).getValue());
            }
            String combinedString = String.join(" ", subStrings);
            JSONObject combinedMeaning = compositionFunction.apply(subContents);
            for (String childRole : childNodeChunks.keySet()){
                addChunkIndices(combinedMeaning, subStrings, childNodeChunks.get(childRole), childRole);
            }

            ans.put(combinedString, combinedMeaning);
        }
        return ans;
    }

    /*
    * A convenience function used for corpus generation
    * It sets up chunk indices in the child inside pathToChild at the appropriate indices
    * given the particular ordered list of chunks and the indices of the chunks contributing to that child
    *
    * The chunk start is the index of the start, the chunk end is the index at the end
    *
    * */
    public static void addChunkIndices(JSONObject composedContent,
                                             List<String> stringChunks,
                                             Pair<Integer, Integer> selectedChunks,
                                             String pathToChild){
        Integer startingIndex = 0;
        for (int i = 0; i < selectedChunks.getKey(); i++) {
            startingIndex += stringChunks.get(i).split(" ").length;
        }
        Integer endingIndex = startingIndex - 1;
        for (int i = selectedChunks.getKey(); i <= selectedChunks.getValue(); i++) {
            endingIndex += stringChunks.get(i).split(" ").length;
        }
        JSONObject tmp = (JSONObject) new SemanticsModel(composedContent).newGetSlotPathFiller(pathToChild);
        tmp.put("chunk-start", startingIndex);
        tmp.put("chunk-end", endingIndex);
    }


}
