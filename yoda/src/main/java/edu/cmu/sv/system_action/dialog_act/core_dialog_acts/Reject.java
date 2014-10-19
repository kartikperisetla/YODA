package edu.cmu.sv.system_action.dialog_act.core_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.Map;

/**
 * Created by cohend on 10/18/14.
 */
public class Reject extends DialogAct {
    @Override
    public Double reward(DiscourseUnit DU) {
        return null;
    }

    @Override
    public Double cost(DiscourseUnit DU) {
        return null;
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
