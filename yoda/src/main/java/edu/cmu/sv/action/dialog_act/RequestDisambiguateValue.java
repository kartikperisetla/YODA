package edu.cmu.sv.action.dialog_act;

import edu.cmu.sv.dialog_management.DiscourseUnit;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/15/14.
 */
public class RequestDisambiguateValue implements DialogAct{
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
}
