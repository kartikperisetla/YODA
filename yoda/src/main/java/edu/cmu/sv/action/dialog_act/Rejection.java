package edu.cmu.sv.action.dialog_act;

import edu.cmu.sv.dialog_management.DiscourseUnit;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;

import java.util.Map;

/**
 * Created by David Cohen on 9/11/14.
 */
public class Rejection implements DialogAct{
    // template "No."

    @Override
    public Double reward(DiscourseUnit DU) {
        return 0.0;
    }

    @Override
    public Double cost(DiscourseUnit DU) {
       return RewardAndCostCalculator.penaltyForContradictingUser;
    }

    @Override
    public Map<String, String> getParameters() {
        return null;
    }

    @Override
    public Map<String, String> getBindings() {
        return null;
    }

    @Override
    public DialogAct bindVariables(Map<String, String> bindings) {
        return null;
    }
}
