package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.utils.Combination;
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
            Function<List<JSONObject>, JSONObject> compositionFunction){
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
            ans.put(combinedString, combinedMeaning);
        }
        return ans;
    }

}
