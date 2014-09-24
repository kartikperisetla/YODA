package edu.cmu.sv.system_action.dialog_act.core_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.Map;

/**
 * Created by David Cohen on 9/11/14.
 */
public class Rejection extends DialogAct {
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
