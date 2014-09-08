package edu.cmu.sv.dialog_management.dialog_act;

import edu.cmu.sv.dialog_management.DiscourseUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class RequestConfirmValue implements DialogAct{
    private Map<String, String> boundVariables = null;
    static Map<String, String> parameters = new HashMap<>();
    static {
        parameters.put("v1", "value");
    }

    // template "<v1> ?"

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
    public Double reward(DiscourseUnit DU) {
        Double expectedConfidence = RewardAndCostCalculator.predictConfidenceAfterValueGain(
                DU, .5, boundVariables.get("v1"), null);
        return expectedConfidence;
    }

    @Override
    public Double cost(DiscourseUnit DU) {
        return 1.5;
    }

    @Override
    public String toString() {
        return "RequestConfirmValue{" + "boundVariables=" + boundVariables +'}';
    }
}
