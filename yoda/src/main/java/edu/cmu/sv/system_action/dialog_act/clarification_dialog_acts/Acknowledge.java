package edu.cmu.sv.system_action.dialog_act.clarification_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/18/14.
 */
public class Acknowledge extends DialogAct {
    @Override
    public Double reward(DiscourseUnit2 DU) {
        return 0.0;
    }

    @Override
    public Double cost(DiscourseUnit2 DU) {
        return 0.0;
    }

    @Override
    public Map<String, Class<? extends Thing>> getParameters() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getBindings() {
        return null;
    }

    @Override
    public void bindVariables(Map<String, Object> bindings) {}
}
