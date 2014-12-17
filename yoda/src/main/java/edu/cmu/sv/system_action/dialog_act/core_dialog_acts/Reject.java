package edu.cmu.sv.system_action.dialog_act.core_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cohend on 10/18/14.
 */
public class Reject extends DialogAct {
    static Map<String, Class<? extends Thing>> individualParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> classParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> descriptionParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> pathParameters = new HashMap<>();
    @Override
    public Map<String, Class<? extends Thing>> getPathParameters() {
        return pathParameters;
    }
    @Override
    public Map<String, Class<? extends Thing>> getDescriptionParameters() {
        return descriptionParameters;
    }
    @Override
    public Map<String, Class<? extends Thing>> getClassParameters() {
        return classParameters;
    }
    @Override
    public Map<String, Class<? extends Thing>> getIndividualParameters() {
        return individualParameters;
    }

    @Override
    public Double reward(DialogState dialogState, DiscourseUnit discourseUnit){
        return (RewardAndCostCalculator.discourseIndependentArgumentationReward(discourseUnit, this) *
                RewardAndCostCalculator.probabilityInterpretedCorrectly(discourseUnit, dialogState, this)) +
                (RewardAndCostCalculator.answerObliged(discourseUnit) &&
                        !RewardAndCostCalculator.answerAlreadyProvided(discourseUnit, dialogState) ?
                        RewardAndCostCalculator.penaltyForIgnoringUserRequest : 0);
    }
}
