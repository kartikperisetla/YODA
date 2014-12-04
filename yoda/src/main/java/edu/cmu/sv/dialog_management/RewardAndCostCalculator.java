package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DialogStateHypothesis;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.DontKnow;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Reject;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
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



    /*
    * Return the probability that this dialog act will be interpreted correctly in this context.
    * If this is a grounding act, duHypothesis is the DU that the system intends to clarify,
    * Otherwise, this is the DU that the system intends to respond to.
    * */
    public static Double contextualAppropriateness(DiscourseUnitHypothesis duHypothesis, DialogStateHypothesis dsHypothesis,
                                                   DialogAct dialogAct){
        Double probabilityInterpretedCorrectly = 1.0;
        for (String discourseUnitIdentifier : dsHypothesis.getDiscourseUnitHypothesisMap().keySet()){
            DiscourseUnitHypothesis otherPredecessor = dsHypothesis.getDiscourseUnitHypothesisMap().get(discourseUnitIdentifier);
            if (otherPredecessor==duHypothesis)
                continue;
            if (otherPredecessor.getInitiator().equals("user") &&
                    otherPredecessor.getMostRecentContributionTime() > duHypothesis.getMostRecentContributionTime())
                probabilityInterpretedCorrectly *= .1;
        }
        return probabilityInterpretedCorrectly;
    }

    /*
    * Return the reward that this act will achieve, accounting for other discourse units in in the dialog state
    * */
    public static Double discourseDependentReward(DiscourseUnitHypothesis duHypothesis, DialogStateHypothesis dsHypothesis,
                                                  DialogAct dialogAct){
        // argumentation: interpret duHypothesis as the predecessor
        // if duHypothesis is a YNQ, spoken by the user, give a reward
        // if duHypothesis has already been answered, give a penalty
        if (!duHypothesis.getInitiator().equals("user"))
            return 0.0;
        String predecessorDialogAct = (String) duHypothesis.getSpokenByThem().newGetSlotPathFiller("dialogAct");
        if (predecessorDialogAct.equals(YNQuestion.class.getSimpleName()) &&
                !dsHypothesis.getArgumentationLinks().stream().anyMatch(
                        x -> dsHypothesis.getDiscourseUnitHypothesisMap().get(x.getPredecessor()).equals(duHypothesis)) &&
                (dialogAct instanceof Accept || dialogAct instanceof Reject || dialogAct instanceof DontKnow)) {
            return 1.0;

        }
        return 0.0;
    }

    /*
    * Return the specific reward that this argumentation act should give,
    * based on the analysis of the predecessor discourse unit
    * */
    public static Double discourseIndependentArgumentationReward(DiscourseUnitHypothesis predecessorDiscourseUnit,
                                                                 DialogAct dialogAct){
//        System.out.println("discourseIndependentArgumentationReward: "+dialogAct);
        Double probabilityCorrectAnswer = 0.0;
        if (dialogAct instanceof Accept) {
            if (predecessorDiscourseUnit.getYnqTruth()!=null)
                probabilityCorrectAnswer = predecessorDiscourseUnit.getYnqTruth();
        } else if (dialogAct instanceof Reject) {
            if (predecessorDiscourseUnit.getYnqTruth()!=null)
                probabilityCorrectAnswer = 1 - predecessorDiscourseUnit.getYnqTruth();
        } else if (dialogAct instanceof DontKnow) {
            if (predecessorDiscourseUnit.getYnqTruth()==null)
                probabilityCorrectAnswer = 1.0;
        }
        return rewardForCorrectAnswer*probabilityCorrectAnswer - penaltyForIncorrectAnswer*(1-probabilityCorrectAnswer);
    }

}
