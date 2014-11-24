package edu.cmu.sv.system_action.dialog_act.core_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class WHQuestion extends DialogAct {
    @Override
    public Double reward(DiscourseUnit2 DU) {
        return null;
    }

    @Override
    public Map<String, Class<? extends Thing>> getParameters() {
        return null;
    }

    @Override
    public Map<String, Object> getBindings() {
        return null;
    }

    @Override
    public void bindVariables(Map<String, Object> bindings) {

    }
}
