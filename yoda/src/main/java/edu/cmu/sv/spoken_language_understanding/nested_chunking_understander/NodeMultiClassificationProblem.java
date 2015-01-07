package edu.cmu.sv.spoken_language_understanding.nested_chunking_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
* Created by David Cohen on 12/30/14.
*/
public class NodeMultiClassificationProblem {
    public Map<String, Map<String, Object>> outputRolesAndFillers;
    public StringDistribution outputDistribution;
    JSONObject surroundingStructure;
    String contextPathInStructure;
    String stringForAnalysis;

    public NodeMultiClassificationProblem(String fullUtteranceText, JSONObject surroundingStructure, String contextPathInStructure) {
        this.surroundingStructure = surroundingStructure;
        this.contextPathInStructure = contextPathInStructure;
        stringForAnalysis = SemanticsModel.extractChunk(surroundingStructure, fullUtteranceText, contextPathInStructure);
    }
}
