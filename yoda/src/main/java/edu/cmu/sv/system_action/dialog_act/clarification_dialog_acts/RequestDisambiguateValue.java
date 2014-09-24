package edu.cmu.sv.system_action.dialog_act.clarification_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/15/14.
 */
public class RequestDisambiguateValue implements DialogAct {
    private Map<String, String> boundVariables = null;
    static Map<String, String> parameters = new HashMap<>();
    static {
        parameters.put("v1", "value");
        parameters.put("v2", "value");
    }


    @Override
    public Double reward(DiscourseUnit DU) {
        try {
            return RewardAndCostCalculator.clarificationDialogActReward(DU,
                    RewardAndCostCalculator.predictConfidenceGainFromValueDisambiguation(DU,
                            boundVariables.get("v1"), boundVariables.get("v2")));
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
        return "RequestDisambiguateValue{" +
                "boundVariables=" + boundVariables +
                '}';
    }
}
