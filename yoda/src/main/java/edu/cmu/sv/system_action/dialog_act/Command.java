package edu.cmu.sv.system_action.dialog_act;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;

import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class Command implements DialogAct {

    // command is not executable by the system
    @Override
    public Double reward(DiscourseUnit DU) {return null;}
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
