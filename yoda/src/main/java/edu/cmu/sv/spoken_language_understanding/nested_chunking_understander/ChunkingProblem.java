package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

/**
* Created by David Cohen on 12/30/14.
*/
public class ChunkingProblem {
    public Map<String, Set<ChunkingProblem>> outputChildChunkingProblems;
    public StringDistribution outputDistribution;
    JSONObject surroundingStructure;
    String contextPathInStructure;
    String fullUtterance;
    Pair<Integer, Integer> chunkingIndices;

    /*
    * How ChunkingProblem gets initialized in different situations:
    *
    * Parsing a result:
    *  - set surroundingStructure <- null
    * */
    public ChunkingProblem(String fullUtteranceText, JSONObject surroundingStructure, String contextPathInStructure, Pair<Integer, Integer> chunkingIndices) {
        this.surroundingStructure = surroundingStructure;
        this.contextPathInStructure = contextPathInStructure;
        this.chunkingIndices = chunkingIndices;
        this.fullUtterance = fullUtteranceText;
    }

    public String stringForAnalysis(){
        return SemanticsModel.extractChunk(surroundingStructure, fullUtterance, contextPathInStructure);
    }
}
