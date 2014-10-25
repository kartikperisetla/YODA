package edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/24/14.
 */
public class RequestVerb extends DialogAct{
    private Map<String, Object> boundVariables = null;
    static Map<String, Class<? extends Thing>> parameters = new HashMap<>(); // parameters are empty for this DA
    static{
        // this is the agent or patient about which we need to know the action
        parameters.put("v1", Verb.class);
    }

    // template: "What about <v1>?"

    @Override
    public Double reward(DiscourseUnit2 DU) {
        return null;
    }

    @Override
    public Double cost(DiscourseUnit2 DU) {
        return null;
    }

    @Override
    public Map<String, Class<? extends Thing>> getParameters() {
        return parameters;
    }

    @Override
    public Map<String, Object> getBindings() {
        return boundVariables;
    }

    @Override
    public void bindVariables(Map<String, Object> bindings) {
        boundVariables = bindings;
    }
}
