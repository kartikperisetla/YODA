package edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.utils.NBestDistribution;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class ConfirmValueSuggestion extends ClarificationDialogAct {
    static Map<String, Object> individualParameters = new HashMap<>();
    static Map<String, Object> classParameters = new HashMap<>();
    static Map<String, Object> descriptionParameters = new HashMap<>();
    static Map<String, Object> pathParameters = new HashMap<>();
    @Override
    public Map<String, Object> getPathParameters() {
        return pathParameters;
    }

    @Override
    public Map<String, Object> getDescriptionParameters() {
        return descriptionParameters;
    }

    @Override
    public Map<String, Object> getClassParameters() {
        return classParameters;
    }
    @Override
    public Map<String, Object> getIndividualParameters() {
        return individualParameters;
    }

    @Override
    public Double clarificationReward(NBestDistribution<DialogState> dialogStateDistribution) {
        return -1.0;
    }
}