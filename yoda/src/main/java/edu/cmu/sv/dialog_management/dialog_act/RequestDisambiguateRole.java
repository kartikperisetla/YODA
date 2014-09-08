package edu.cmu.sv.dialog_management.dialog_act;

import edu.cmu.sv.dialog_management.DiscourseUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class RequestDisambiguateRole implements DialogAct {
    private Map<String, String> boundVariables = null;
    static Map<String, String> parameters = new HashMap<>();
    static {
        parameters.put("r1", "role");
        parameters.put("r2", "role");
    }

    // Template: "<r1>, or <r2>?"

    @Override
    public Double reward(DiscourseUnit DU) {
        // slightly more likely to improve confidence for r1 than r2
        Double expectedConfidence = RewardAndCostCalculator.predictConfidenceAfterRoleGain(
                DU, .5, boundVariables.get("r1"), null);
        expectedConfidence = RewardAndCostCalculator.predictConfidenceAfterRoleGain(
                DU, .4, boundVariables.get("r2"), expectedConfidence);
        return expectedConfidence;
    }

    @Override
    public Double cost(DiscourseUnit DU) {
        return 1.0;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public Map<String, String> getBindings() {
        return boundVariables;
    }

    @Override
    public DialogAct bindVariables(Map<String, String> bindings) {
        boundVariables = bindings;
        return this;
    }

    @Override
    public String toString() {
        return "RequestDisambiguateRole{" + "boundVariables=" + boundVariables +'}';
    }

}
