package edu.cmu.sv.action.dialog_act;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;

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
        try {
            return RewardAndCostCalculator.clarificationDialogActReward(DU,
                    RewardAndCostCalculator.predictConfidenceGainFromRoleDisambiguation(DU,
                            boundVariables.get("r1"), boundVariables.get("r2")));
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double cost(DiscourseUnit DU) {
        // we oblige the user to a single phrase response
       return RewardAndCostCalculator.penaltyForObligingUserPhrase*1 +
               RewardAndCostCalculator.penaltyForSpeakingPhrase *2;
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
