package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DialogStateHypothesis;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.DontKnow;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Reject;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTaskPreferences;
import edu.cmu.sv.utils.StringDistribution;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/8/14.
 *
 * Contains standard values and functions that are used to compute utility for possible system actions
 *
 * The basic rule-of-thumb is that 1 reward ~= the reward for successfully executing a dialog task.
 * To set a reward for some other condition, estimate the relative importance of that condition
 * compared to successfully completing a dialog task.
 *
 */
public class RewardAndCostCalculator {
    public static double penaltyForSpeakingPhrase = .1;
    public static double penaltyForIgnoringUserRequest = 2;
    public static double rewardForCorrectAnswer = 5;
    public static double penaltyForIncorrectAnswer = 5;


    public static Double discourseDependentReward(DiscourseUnitHypothesis duHypothesis, DialogStateHypothesis dsHypothesis,
                                                  DialogAct dialogAct){
        return 1.0;
    }

    public static Double discourseIndependentArgumentationReward(DiscourseUnitHypothesis duHypothesis, DialogAct dialogAct){
//        System.out.println("discourseIndependentArgumentationReward: "+dialogAct);
        Double probabilityCorrectAnswer = 0.0;
        if (dialogAct instanceof Accept) {
            if (duHypothesis.getYnqTruth()!=null)
                probabilityCorrectAnswer = duHypothesis.getYnqTruth();
        } else if (dialogAct instanceof Reject) {
            if (duHypothesis.getYnqTruth()!=null)
                probabilityCorrectAnswer = 1 - duHypothesis.getYnqTruth();
        } else if (dialogAct instanceof DontKnow) {
            if (duHypothesis.getYnqTruth()==null)
                probabilityCorrectAnswer = 1.0;
        }
        return rewardForCorrectAnswer*probabilityCorrectAnswer - penaltyForIncorrectAnswer*(1-probabilityCorrectAnswer);
    }

}
