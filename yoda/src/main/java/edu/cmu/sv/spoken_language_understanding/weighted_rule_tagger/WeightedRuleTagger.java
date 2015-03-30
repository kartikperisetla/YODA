package edu.cmu.sv.spoken_language_understanding.weighted_rule_tagger;


import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.utils.StringDistribution;

import java.util.*;

/**
 * Created by David Cohen on 3/27/15.
 */
public class WeightedRuleTagger {
    Map<TaggingRule, Double> taggingRuleWeights;

    NBestDistribution<List<String>> tag(List<String> tokens){
        NBestDistribution<List<String>> taggingResult = new NBestDistribution<>();

        for (int i = 0; i < tokens.size(); i++) {
            StringDistribution tagDistribution = new StringDistribution();
            for (TaggingRule key : taggingRuleWeights.keySet()){
                tagDistribution.increment(key.applyRuleAtIndex(tokens, i), taggingRuleWeights.get(key));
            }
        }
        return taggingResult;

    }

    public NBestDistribution<ChunkingResult> getChunkingResult(List<String> tokens){
        NBestDistribution<List<String>> tagResults = tag(tokens);
        NBestDistribution<ChunkingResult> ans = new NBestDistribution<>();
        for (List<String> labels : tagResults.keySet()){
            ans.put(new ChunkingResult(tokens, labels), tagResults.get(labels));
        }
        return ans;
    }


    class ChunkingResult {
        // map chunk label to
        Map<String, List<String>> chunkMap = new HashMap<>();

        /*
        * This type of chunk accumulation DOES NOT USE B-I-O format,
        * It appends any token with the given label to that chunk
        * */
        public ChunkingResult(List<String> tokens, List<String> labels) {
            for (int i = 0; i < labels.size(); i++) {
                List<String> chunkWithLabel = chunkMap.getOrDefault(labels.get(i), new LinkedList<>());
                chunkWithLabel.add(tokens.get(i));
            }
        }
    }

}
