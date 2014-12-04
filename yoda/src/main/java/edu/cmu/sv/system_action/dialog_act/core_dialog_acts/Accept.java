package edu.cmu.sv.system_action.dialog_act.core_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogStateHypothesis;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/18/14.
 */
public class Accept extends DialogAct {
    static Map<String, Class<? extends Thing>> individualParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> classParameters = new HashMap<>();
    @Override
    public Map<String, Class<? extends Thing>> getClassParameters() {
        return classParameters;
    }
    @Override
    public Map<String, Class<? extends Thing>> getIndividualParameters() {
        return individualParameters;
    }

    @Override
    public Double reward(DialogStateHypothesis dialogStateHypothesis, DiscourseUnitHypothesis discourseUnitHypothesis){
        return RewardAndCostCalculator.discourseIndependentArgumentationReward(discourseUnitHypothesis, this) *
                RewardAndCostCalculator.probabilityInterpretedCorrectly(discourseUnitHypothesis, dialogStateHypothesis, this) +
                (RewardAndCostCalculator.answerObliged(discourseUnitHypothesis, dialogStateHypothesis) ?
                1.0 : 0);
    }

}
