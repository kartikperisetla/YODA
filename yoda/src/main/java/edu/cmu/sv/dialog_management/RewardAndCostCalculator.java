package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DialogStateHypothesis;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.dialog_state_tracking.Utils;
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
    public static double penaltyForSpeaking = .5;
    public static double penaltyForIgnoringUserRequest = 2;
    public static double rewardForCorrectAnswer = 5;
    public static double penaltyForIncorrectAnswer = 5;



    /*
    * Return the probability that this dialog act will be interpreted in this context.
    *
    * If this is a grounding act, duHypothesis is the DU that the system intends to clarify,
    * Otherwise, this is the DU that the system intends to respond to.
    * */
    public static Double probabilityInterpretedCorrectly(DiscourseUnitHypothesis duHypothesis, DialogStateHypothesis dsHypothesis,
                                                         DialogAct dialogAct){
        if (dialogAct instanceof Accept || dialogAct instanceof Reject || dialogAct instanceof DontKnow) {
            if (!duHypothesis.getInitiator().equals("user"))
                return 0.0;
            double probabilityInterpretedThisWay =
                    Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(duHypothesis, dsHypothesis, "user"));
            if (answerObliged(duHypothesis) && !answerAlreadyProvided(duHypothesis, dsHypothesis))
                return probabilityInterpretedThisWay;
            else if (answerObliged(duHypothesis))
                return .1 * probabilityInterpretedThisWay;
        }
        return 0.0;
    }

    public static boolean answerAlreadyProvided(DiscourseUnitHypothesis predecessor, DialogStateHypothesis dsHypothesis){
        return answerObliged(predecessor) &&
                dsHypothesis.getArgumentationLinks().stream().anyMatch(
                        x -> dsHypothesis.getDiscourseUnitHypothesisMap().get(x.getPredecessor()).equals(predecessor));
    }

    /*
    * Return weather or not the predecessor obliges a response which has not been given
    * */
    public static boolean answerObliged(DiscourseUnitHypothesis predecessor){
        String predecessorDialogAct;
        if (predecessor.getInitiator().equals("user"))
            predecessorDialogAct = (String) predecessor.getSpokenByThem().newGetSlotPathFiller("dialogAct");
        else
            predecessorDialogAct = (String) predecessor.getSpokenByMe().newGetSlotPathFiller("dialogAct");
        return predecessorDialogAct.equals(YNQuestion.class.getSimpleName());
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




    /*
    * To compute the reward for a clarification dialog act,
    * estimate the improvement in reward to all possible dialog and non-dialog tasks
    * that relate to all the available DU hypotheses.
    * */
    public static Double clarificationDialogActReward(Database db, DiscourseUnitHypothesis discourseUnitHypothesis,
                                                      StringDistribution predictedRelativeConfidenceGain)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Double totalReward = 0.0;

        // sum up the predicted rewards supposing that each current hypothesis is true,
        // weighting the predicted reward by the current belief that the hypothesis is true.
        for (String hypothesisID : discourseUnitHypothesis.getHypotheses().keySet()){
            DiscourseUnit2.DiscourseUnitHypothesis hypothesis = discourseUnitHypothesis.getHypotheses().get(hypothesisID);
            SemanticsModel spokenByThem = hypothesis.getSpokenByThem();
            Double currentConfidence = discourseUnitHypothesis.getHypothesisDistribution().get(hypothesisID);
            Double predictedConfidence = currentConfidence + (1-currentConfidence)*
                    predictedRelativeConfidenceGain.get(hypothesisID);

            // predict the difference in expected reward after clarification
            Double predictedRewardDifference = 0.0;
            Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                    get((String)spokenByThem.newGetSlotPathFiller("dialogAct"));

            // add contribution from non-dialog tasks
            if (DialogRegistry.nonDialogTaskRegistry.containsKey(daClass)) {
                for (Class<? extends NonDialogTask> taskClass : DialogRegistry.nonDialogTaskRegistry.get(daClass)) {
                    NonDialogTaskPreferences preferences = taskClass.getConstructor(Database.class).newInstance(db).getPreferences();
                    predictedRewardDifference += predictedConfidence * preferences.rewardForCorrectExecution;
                    predictedRewardDifference -= (1 - predictedConfidence) * preferences.penaltyForIncorrectExecution;
                    predictedRewardDifference -= currentConfidence * preferences.rewardForCorrectExecution;
                    predictedRewardDifference += (1 - currentConfidence) * preferences.penaltyForIncorrectExecution;
                    totalReward += currentConfidence * predictedRewardDifference /
                            DialogRegistry.nonDialogTaskRegistry.get(daClass).size();
                }
            }
        }
        return totalReward;
    }


    /*
    * Confirming a value is confirming that some role is filled by it,
    * it does not confirm anything about which role it fills
    * */
    public static StringDistribution predictConfidenceGainFromValueConfirmation(DiscourseUnitHypothesis discourseUnitHypothesis,
                                                                                Object value){
        double limit = .8; // we will never predict 100% confidence gain
        StringDistribution ans = new StringDistribution();
        if (!(value instanceof JSONObject))
            return null;
        Map<String, Boolean> hasValueMap = discourseUnitHypothesis.getHypotheses().keySet().stream().collect(Collectors.toMap(
                x->x,
                x->!discourseUnitHypothesis.getHypotheses().get(x).getSpokenByThem().
                        findAllPathsToNonConflict((JSONObject)value).isEmpty()
        ));

        for (String key : discourseUnitHypothesis.getHypotheses().keySet()){
            if (discourseUnitHypothesis.getHypothesisDistribution().get(key) >= 1.0) {
                ans.put(key, 0.0);
            }
            else if (hasValueMap.get(key)) {
                ans.put(key, limit *
                        discourseUnitHypothesis.getHypotheses().keySet().stream().
                                filter(x -> !hasValueMap.get(x)).
                                map(x -> discourseUnitHypothesis.getHypothesisDistribution().get(x)).
                                reduce(0.0, (x, y) -> x + y) * 1.0 /
                        (1.0 - discourseUnitHypothesis.getHypothesisDistribution().get(key)) /
                        hasValueMap.values().stream().filter(x -> x).count());
            } else {
                ans.put(key, 0.0);
            }
        }
//        System.out.println("RewardAndCostCalculator.predictConfidenceGainFromValueConfirmation: ans:\n"+ans);
        return ans;
    }

}
