package edu.cmu.sv.action.dialog_act;

import edu.cmu.sv.dialog_management.DiscourseUnit;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class RequestRephrase implements DialogAct {
    private Map<String, String> boundVariables = null;
    static Map<String, String> parameters = new HashMap<>(); // parameters are empty for this DA

    @Override
    public Double reward(DiscourseUnit DU) {
        // expect a large improvement in overall top joint confidence
        return RewardAndCostCalculator.predictConfidenceAfterJointGain(DU, .7, null);
    }

    @Override
    public Double cost(DiscourseUnit DU) {
        // this dialog act imposes a high cost because it requires significant effort from the user
        return 2.0;
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
        return "RequestRephrase{" +
                "boundVariables=" + boundVariables +
                '}';
    }
}
