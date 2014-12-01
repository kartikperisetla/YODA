package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
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
    public static double penaltyForIncorrectAnswer = 10;

    public static Double nonDialogTaskReward(DiscourseUnitHypothesis DU, NonDialogTask nonDialogTask){
        Double totalReward = 0.0;
        Double probabilityCorrect = 0.0;
        for (String key : DU.getHypotheses().keySet()){
            if (nonDialogTask.meetsTaskSpec(DU.getHypotheses().get(key).getSpokenByThem()))
                probabilityCorrect += DU.getHypothesisDistribution().get(key);
        }
        // multiply probability correct by probability that the task is executable
        probabilityCorrect *= nonDialogTask.assessExecutability();

        // we add the penalty for delay to the reward, because that is equivalent to subtracting it
        // from all other actions under consideration
        totalReward += probabilityCorrect * (nonDialogTask.getPreferences().rewardForCorrectExecution +
                nonDialogTask.getPreferences().penaltyForDelay);
        totalReward -= (1.0-probabilityCorrect) * nonDialogTask.getPreferences().penaltyForIncorrectExecution;

        return totalReward;
    }

    public static Double discourseDependentReward(){

    }

    public static Double discourseIndependentArgumentationReward(DiscourseUnitHypothesis DU, DialogAct dialogAct){
        Double probabilityCorrectAnswer = 0.0;
        for (String hypothesisID : DU.getHypotheses().keySet()){
            Double duHypothesisProbability = DU.getHypothesisDistribution().get(hypothesisID);
            DiscourseUnitHypothesis.DiscourseUnitHypothesis hypothesis = DU.getHypotheses().get(hypothesisID);
            StringDistribution distribution = hypothesis.getGnd().getGroundedHypothesesDistribution();
            if (dialogAct instanceof Accept){
                for (String groundedHypothesis : hypothesis.getGnd().getYnqTruth().keySet()){
                    probabilityCorrectAnswer += duHypothesisProbability *
                            (distribution.get(groundedHypothesis) *
                            hypothesis.getGnd().getYnqTruth().get(groundedHypothesis));
                }
            } else if (dialogAct instanceof Reject){
                for (String groundedHypothesis : hypothesis.getGnd().getYnqTruth().keySet()){
                    probabilityCorrectAnswer += duHypothesisProbability * (
                            distribution.get(groundedHypothesis) *
                            (1 - hypothesis.getGnd().getYnqTruth().get(groundedHypothesis)));
                }
            }

        }
        return rewardForCorrectAnswer*probabilityCorrectAnswer - penaltyForIncorrectAnswer*(1-probabilityCorrectAnswer);
    }


    /*
    * To compute the reward for a clarification dialog act,
    * estimate the improvement in reward to all possible dialog and non-dialog tasks
    * that relate to all the available DU hypotheses.
    * */
    public static Double clarificationDialogActReward(Database db, DiscourseUnitHypothesis DU,
                                                      StringDistribution predictedRelativeConfidenceGain)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Double totalReward = 0.0;

        // sum up the predicted rewards supposing that each current hypothesis is true,
        // weighting the predicted reward by the current belief that the hypothesis is true.
        for (String hypothesisID : DU.getHypotheses().keySet()){
            DiscourseUnitHypothesis.DiscourseUnitHypothesis hypothesis = DU.getHypotheses().get(hypothesisID);
            SemanticsModel spokenByThem = hypothesis.getSpokenByThem();
            Double currentConfidence = DU.getHypothesisDistribution().get(hypothesisID);
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

    public static StringDistribution predictedConfidenceGainFromJointClarification(DiscourseUnitHypothesis DU){
        double relativeImprovement = .5; // every hypothesis is expected to improve by 50% relative if it is correct
        StringDistribution ans = new StringDistribution();
        for (String key : DU.getHypotheses().keySet()){
            ans.put(key, relativeImprovement);
        }
        return ans;
    }

    /*
    * Confirming a value is confirming that some role is filled by it,
    * it does not confirm anything about which role it fills
    * */
    public static StringDistribution predictConfidenceGainFromValueConfirmation(DiscourseUnitHypothesis DU, Object value){
        double limit = .8; // we will never predict 100% confidence gain
        StringDistribution ans = new StringDistribution();
        if (!(value instanceof JSONObject))
            return null;
        Map<String, Boolean> hasValueMap = DU.getHypotheses().keySet().stream().collect(Collectors.toMap(
                x->x,
                x->!DU.getHypotheses().get(x).getSpokenByThem().
                        findAllPathsToNonConflict((JSONObject)value).isEmpty()
        ));

        for (String key : DU.getHypotheses().keySet()){
            if (DU.getHypothesisDistribution().get(key) >= 1.0) {
                ans.put(key, 0.0);
            }
            else if (hasValueMap.get(key)) {
                ans.put(key, limit *
                        DU.getHypotheses().keySet().stream().
                                filter(x -> !hasValueMap.get(x)).
                                map(x -> DU.getHypothesisDistribution().get(x)).
                                reduce(0.0, (x, y) -> x + y) * 1.0 /
                        (1.0 - DU.getHypothesisDistribution().get(key)) /
                        hasValueMap.values().stream().filter(x -> x).count());
            } else {
                ans.put(key, 0.0);
            }
        }
//        System.out.println("RewardAndCostCalculator.predictConfidenceGainFromValueConfirmation: ans:\n"+ans);
        return ans;
    }

}
