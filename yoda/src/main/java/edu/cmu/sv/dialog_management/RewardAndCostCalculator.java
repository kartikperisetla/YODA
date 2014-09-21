package edu.cmu.sv.dialog_management;

import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_task.DialogTask;
import edu.cmu.sv.system_action.dialog_task.DialogTaskPreferences;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTaskPreferences;
import edu.cmu.sv.utils.StringDistribution;

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
    public static double penaltyForContradictingUser = .5;
    public static double penaltyForObligingUserAction = 2;
    public static double penaltyForObligingUserPhrase = .1;
    public static double penaltyForSpeakingPhrase = .1;

    public static Double nonDialogTaskReward(DiscourseUnit DU, NonDialogTask nonDialogTask){
        Double totalReward = 0.0;
        Double probabilityCorrect = 0.0;
        for (String key : DU.getHypotheses().keySet()){
            if (nonDialogTask.meetsTaskSpec(DU.getHypotheses().get(key)))
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

    public static Double dialogTaskReward(DiscourseUnit DU, DialogTask dialogTask){
        Double totalReward = 0.0;
        Double probabilityCorrect = 0.0;
        for (String key : DU.getHypotheses().keySet()){
            if (dialogTask.meetsTaskSpec(DU.getHypotheses().get(key)))
                probabilityCorrect += DU.getHypothesisDistribution().get(key);
        }
        // multiply probability correct by probability that the task is executable
        probabilityCorrect *= dialogTask.assessExecutability();

        // we add the penalty for delay to the reward, because that is equivalent to subtracting it
        // from all other actions under consideration
        totalReward += probabilityCorrect * (dialogTask.getPreferences().rewardForCorrectExecution +
            dialogTask.getPreferences().penaltyForDelay);
        totalReward -= (1.0-probabilityCorrect) * dialogTask.getPreferences().penaltyForIncorrectExecution;

        return totalReward;
    }

    /*
    * To compute the reward for a clarification dialog act,
    * estimate the improvement in reward to all possible dialog and non-dialog tasks
    * that relate to all the available DU hypotheses.
    * */
    public static Double clarificationDialogActReward(DiscourseUnit DU,
                                                      StringDistribution predictedRelativeConfidenceGain)
            throws IllegalAccessException, InstantiationException {
        Double totalReward = 0.0;

        // sum up the predicted rewards supposing that each current hypothesis is true,
        // weighting the predicted reward by the current belief that the hypothesis is true.
        for (String hypothesisID : DU.getHypotheses().keySet()){
            SemanticsModel hypothesis = DU.getHypotheses().get(hypothesisID);
            Double currentConfidence = DU.getHypothesisDistribution().get(hypothesisID);
            Double predictedConfidence = currentConfidence + (1-currentConfidence)*
                    predictedRelativeConfidenceGain.get(hypothesisID);

            // predict the difference in expected reward after clarification
            Double predictedRewardDifference = 0.0;
            Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                    get(hypothesis.getSlotPathFiller("dialogAct"));
            // add contribution from dialog tasks
            if (DialogRegistry.dialogTaskRegistry.containsKey(daClass)) {
                for (Class<? extends DialogTask> taskClass : DialogRegistry.dialogTaskRegistry.get(daClass)) {
                    DialogTaskPreferences preferences = taskClass.newInstance().getPreferences();
                    predictedRewardDifference += predictedConfidence * preferences.rewardForCorrectExecution;
                    predictedRewardDifference -= (1 - predictedConfidence) * preferences.penaltyForIncorrectExecution;
                    predictedRewardDifference -= currentConfidence * preferences.rewardForCorrectExecution;
                    predictedRewardDifference += (1 - currentConfidence) * preferences.penaltyForIncorrectExecution;
                    totalReward += currentConfidence * predictedRewardDifference /
                            DialogRegistry.dialogTaskRegistry.get(daClass).size();
                }
            }
            // add contribution from non-dialog tasks
            if (DialogRegistry.nonDialogTaskRegistry.containsKey(daClass)) {
                for (Class<? extends NonDialogTask> taskClass : DialogRegistry.nonDialogTaskRegistry.get(daClass)) {
                    NonDialogTaskPreferences preferences = taskClass.newInstance().getPreferences();
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

    public static StringDistribution predictedConfidenceGainFromJointClarification(DiscourseUnit DU){
        double relativeImprovement = .5; // every hypothesis is expected to improve by 50% relative if it is correct
        StringDistribution ans = new StringDistribution();
        for (String key : DU.getHypotheses().keySet()){
            ans.put(key, relativeImprovement);
        }
        return ans;
    }

    public static StringDistribution predictConfidenceGainFromValueDisambiguation(DiscourseUnit DU, String v1,
                                                                                  String v2){
        double limit = .9;
        StringDistribution ans = new StringDistribution();
        Map<String, Boolean> hasV1Map = DU.getHypotheses().keySet().stream().collect(Collectors.toMap(
                x->x, x->DU.getHypotheses().get(x).getAllNonSpecialSlotFillerLeafPairs().values().contains(v1)));
        Map<String, Boolean> hasV2Map = DU.getHypotheses().keySet().stream().collect(Collectors.toMap(
                x->x, x->DU.getHypotheses().get(x).getAllNonSpecialSlotFillerLeafPairs().values().contains(v2)));

        for (String key : DU.getHypotheses().keySet()) {
            if (DU.getHypothesisDistribution().get(key) >= 1.0) {
                ans.put(key, 0.0);
            }
            else if ((hasV1Map.get(key) && hasV2Map.get(key)) || (!hasV1Map.get(key) && !hasV2Map.get(key)))
                ans.put(key, 0.0);
            else if (hasV1Map.get(key)){ // !hasV2Map.get(key) is necessarily true
                ans.put(key, limit *
                        DU.getHypothesisDistribution().keySet().stream().
                                filter(x -> !hasV1Map.get(x)).
                                map(x -> DU.getHypothesisDistribution().get(x)).
                                reduce(0.0, (x, y) -> x + y) * 1.0 /
                        (1.0 - DU.getHypothesisDistribution().get(key)) /
                        DU.getHypothesisDistribution().keySet().stream().
                                filter(x -> hasV1Map.get(x) && !hasV2Map.get(x)).
                                count());
            }
            else if (hasV2Map.get(key)){
                ans.put(key, limit *
                        DU.getHypothesisDistribution().keySet().stream().
                                filter(x -> !hasV2Map.get(x)).
                                map(x -> DU.getHypothesisDistribution().get(x)).
                                reduce(0.0, (x, y) -> x + y) * 1.0 /
                        (1.0 - DU.getHypothesisDistribution().get(key)) /
                        DU.getHypothesisDistribution().keySet().stream().
                                filter(x -> hasV2Map.get(x) && !hasV1Map.get(x)).
                                count());
            }
        }

            return ans;
    }

    public static StringDistribution predictConfidenceGainFromRoleDisambiguation(DiscourseUnit DU, String r1,
                                                                                  String r2){
        double limit = .9;
        StringDistribution ans = new StringDistribution();
        Map<String, Boolean> hasR1Map = DU.getHypotheses().keySet().stream().collect(Collectors.toMap(
                x->x, x->DU.getHypotheses().get(x).getAllNonSpecialSlotFillerLeafPairs().keySet().contains(r1)));
        Map<String, Boolean> hasR2Map = DU.getHypotheses().keySet().stream().collect(Collectors.toMap(
                x->x, x->DU.getHypotheses().get(x).getAllNonSpecialSlotFillerLeafPairs().keySet().contains(r2)));

        for (String key : DU.getHypotheses().keySet()) {
            if (DU.getHypothesisDistribution().get(key) >= 1.0) {
                ans.put(key, 0.0);
            }
            else if ((hasR1Map.get(key) && hasR2Map.get(key)) || (!hasR1Map.get(key) && !hasR2Map.get(key)))
                ans.put(key, 0.0);
            else if (hasR1Map.get(key)){ // !hasR2Map.get(key) is necessarily true
                ans.put(key, limit *
                        DU.getHypothesisDistribution().keySet().stream().
                                filter(x -> !hasR1Map.get(x)).
                                map(x -> DU.getHypothesisDistribution().get(x)).
                                reduce(0.0, (x, y) -> x + y) * 1.0 /
                        (1.0 - DU.getHypothesisDistribution().get(key)) /
                        DU.getHypothesisDistribution().keySet().stream().
                                filter(x -> hasR1Map.get(x) && !hasR2Map.get(x)).
                                count());
            }
            else if (hasR2Map.get(key)){
                ans.put(key, limit *
                        DU.getHypothesisDistribution().keySet().stream().
                                filter(x -> !hasR2Map.get(x)).
                                map(x -> DU.getHypothesisDistribution().get(x)).
                                reduce(0.0, (x, y) -> x + y) * 1.0 /
                        (1.0 - DU.getHypothesisDistribution().get(key)) /
                        DU.getHypothesisDistribution().keySet().stream().
                                filter(x -> hasR2Map.get(x) && !hasR1Map.get(x)).
                                count());
            }
        }

        return ans;
    }

    /*
    * Confirming a value is confirming that some role is filled by it,
    * it does not confirm anything about which role it fills
    * */
    public static StringDistribution predictConfidenceGainFromValueConfirmation(DiscourseUnit DU, String value){
        double limit = .8; // we will never predict 100% confidence gain
        StringDistribution ans = new StringDistribution();
        Map<String, Boolean> hasValueMap = DU.getHypotheses().keySet().stream().collect(Collectors.toMap(
                x->x, x->DU.getHypotheses().get(x).getAllNonSpecialSlotFillerLeafPairs().values().contains(value)
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

    /*
    * Confirming a role is confirming that the role is filled,
    * it does not confirm anything about the value that fills it
    * */
    public static StringDistribution predictConfidenceGainFromRoleConfirmation(DiscourseUnit DU, String role){
        double limit = .8;
        StringDistribution ans = new StringDistribution();
        Map<String, Boolean> roleFilledMap = DU.getHypotheses().keySet().stream().collect(Collectors.toMap(
                x->x, x->DU.getHypotheses().get(x).getAllNonSpecialSlotFillerLeafPairs().keySet().contains(role)
        ));
        for (String key : DU.getHypotheses().keySet()){
            if (DU.getHypothesisDistribution().get(key) >= 1.0)
                ans.put(key, 0.0);
            else if (roleFilledMap.get(key)) {
                ans.put(key, limit *
                        DU.getHypotheses().keySet().stream().
                                filter(x -> !roleFilledMap.get(x)).
                                map(x -> DU.getHypothesisDistribution().get(x)).
                                reduce(0.0, (x, y) -> x + y) * 1.0 /
                        (1.0 - DU.getHypothesisDistribution().get(key)) /
                        roleFilledMap.values().stream().filter(x -> x).count());
            } else {
                ans.put(key, 0.0);
            }
        }
        return ans;
    }

}
