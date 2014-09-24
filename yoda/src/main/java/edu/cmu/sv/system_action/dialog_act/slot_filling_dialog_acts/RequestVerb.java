package edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/24/14.
 */
public class RequestVerb implements DialogAct{
    private Map<String, String> boundVariables = null;
    static Map<String, String> parameters = new HashMap<>(); // parameters are empty for this DA
    static{
        parameters.put("v1", "value"); // this is the agent or patient about which we need to know the action
    }

    // template: "What about <v1>?"

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
