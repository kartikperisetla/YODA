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
    String stringForAnalysis;
    String fullUtterance;
    Pair<Integer, Integer> chunkingIndices;

    public ChunkingProblem(String fullUtteranceText, JSONObject surroundingStructure, String contextPathInStructure, Pair<Integer, Integer> chunkingIndices) {
        this.surroundingStructure = surroundingStructure;
        this.contextPathInStructure = contextPathInStructure;
        this.chunkingIndices = chunkingIndices;
        this.fullUtterance = fullUtteranceText;
        stringForAnalysis = SemanticsModel.extractChunk(surroundingStructure, fullUtteranceText, contextPathInStructure);
    }

    public void runChunker(){
        // create features
        // check cache
        // call external chunking program
        // read results
        // interpret as new chunking problems
        // cache results
    }
}
