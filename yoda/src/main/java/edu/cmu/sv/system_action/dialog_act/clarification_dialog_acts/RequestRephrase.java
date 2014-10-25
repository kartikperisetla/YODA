package edu.cmu.sv.system_action.dialog_act.clarification_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class RequestRephrase extends DialogAct {
    private Map<String, Object> boundVariables = null;
    static Map<String, Class <? extends Thing>> parameters = new HashMap<>(); // parameters are empty for this DA

    @Override
    public Double reward(DiscourseUnit2 DU) {
        try{
        return RewardAndCostCalculator.clarificationDialogActReward(db, DU,
                RewardAndCostCalculator.predictedConfidenceGainFromJointClarification(DU));
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Double cost(DiscourseUnit2 DU) {
        // a complete rephrase will typically involve Subj + Obj + Verb
        return RewardAndCostCalculator.penaltyForObligingUserPhrase*3 +
                RewardAndCostCalculator.penaltyForSpeakingPhrase *1;
    }

    @Override
    public Map<String, Class <? extends Thing>> getParameters() {
        return parameters;
    }

    @Override
    public Map<String, Object> getBindings() {
        return boundVariables;
    }

    @Override
    public DialogAct bindVariables(Map<String, Object> bindings) {
        boundVariables = bindings;
        return this;
    }
}
