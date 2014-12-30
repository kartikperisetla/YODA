package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
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

    public ChunkingProblem(String fullUtteranceText, JSONObject surroundingStructure, String contextPathInStructure) {
        this.surroundingStructure = surroundingStructure;
        this.contextPathInStructure = contextPathInStructure;
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
