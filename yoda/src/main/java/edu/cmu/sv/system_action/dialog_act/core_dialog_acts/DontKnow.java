package edu.cmu.sv.system_action.dialog_act.core_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Utils;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/18/14.
 */
public class DontKnow extends DialogAct {
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
    public Double reward(DialogState dialogState, DiscourseUnit discourseUnit) {
        double statementReward = RewardAndCostCalculator.discourseIndependentStatementReward(this, discourseUnit);
        double probabilityInterpretedCorrectly = Utils.discourseUnitContextProbability(dialogState, discourseUnit);
        boolean answerObliged = RewardAndCostCalculator.answerObliged(discourseUnit);
        boolean answerNotProvided = !RewardAndCostCalculator.answerAlreadyProvided(discourseUnit, dialogState);

//        System.out.println("Statement reward: " + statementReward +
//                ", probability interpreted correctly:" + probabilityInterpretedCorrectly +
//        ", answer obliged:" + answerObliged + ", answer not provided:" + answerNotProvided);
        double ans = ( statementReward * probabilityInterpretedCorrectly) +
                (answerObliged && answerNotProvided ? RewardAndCostCalculator.penaltyForIgnoringUserRequest : 0);
//        System.out.println("Statement reward:" + ans);
        return ans;



    }
}
