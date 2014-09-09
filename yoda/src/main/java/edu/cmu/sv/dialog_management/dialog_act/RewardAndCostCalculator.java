package edu.cmu.sv.dialog_management.dialog_act;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.dialog_management.DiscourseUnit;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.task_interface.dialog_task.DialogTask;
import edu.cmu.sv.task_interface.dialog_task.DialogTaskPreferences;
import edu.cmu.sv.task_interface.non_dialog_task.NonDialogTask;
import edu.cmu.sv.task_interface.non_dialog_task.NonDialogTaskPreferences;
import edu.cmu.sv.utils.StringDistribution;

import java.util.Set;

/**
 * Created by David Cohen on 9/8/14.
 *
 * Contains standard functions that are used to compute reward and utility for possible system actions
 *
 */
public class RewardAndCostCalculator {

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
    public static Double clarificationDialogActReward(DiscourseUnit DU, Double predictedRelativeConfidenceGain)
            throws IllegalAccessException, InstantiationException {
        Double totalReward = 0.0;

        // sum up the predicted rewards supposing that each current hypothesis is true,
        // weighting the predicted reward by the current belief that the hypothesis is true.
        for (String hypothesisID : DU.getHypotheses().keySet()){
            SemanticsModel hypothesis = DU.getHypotheses().get(hypothesisID);
            Double currentConfidence = DU.getHypothesisDistribution().get(hypothesisID);
            Double predictedConfidence = currentConfidence + (1-currentConfidence)*predictedRelativeConfidenceGain;

            // predict the difference in expected reward after clarification
            Double predictedRewardDifference = 0.0;
            Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                    get(hypothesis.getSlotPathFiller("dialogAct"));
            // add contribution from dialog tasks
            for (Class<? extends DialogTask> taskClass : DialogRegistry.dialogTaskRegistry.get(daClass)){
                DialogTaskPreferences preferences = taskClass.newInstance().getPreferences();
                predictedRewardDifference += predictedConfidence * preferences.rewardForCorrectExecution;
                predictedRewardDifference -= (1 - predictedConfidence) * preferences.penaltyForIncorrectExecution;
                predictedRewardDifference -= currentConfidence * preferences.rewardForCorrectExecution;
                predictedRewardDifference += (1 - currentConfidence) * preferences.penaltyForIncorrectExecution;
                totalReward += currentConfidence * predictedRewardDifference /
                        DialogRegistry.dialogTaskRegistry.get(daClass).size();
            }
            // add contribution from non-dialog tasks
            for (Class<? extends NonDialogTask> taskClass : DialogRegistry.nonDialogTaskRegistry.get(daClass)){
                NonDialogTaskPreferences preferences = taskClass.newInstance().getPreferences();
                predictedRewardDifference += predictedConfidence * preferences.rewardForCorrectExecution;
                predictedRewardDifference -= (1 - predictedConfidence) * preferences.penaltyForIncorrectExecution;
                predictedRewardDifference -= currentConfidence * preferences.rewardForCorrectExecution;
                predictedRewardDifference += (1 - currentConfidence) * preferences.penaltyForIncorrectExecution;
                totalReward += currentConfidence * predictedRewardDifference /
                        DialogRegistry.nonDialogTaskRegistry.get(daClass).size();
            }
        }
        return totalReward;
    }





    public static Double predictConfidenceAfterJointGain(DiscourseUnit DU, Double factor, Double topJointConfidence){
        assert 0.0 <= factor && 1.0 >= factor;
        String topJointHypothesis = DU.getHypothesisDistribution().getTopHypothesis();
        topJointConfidence = topJointConfidence == null ?
                DU.getHypothesisDistribution().get(topJointHypothesis) :
                topJointConfidence;
        Double maxJointImprovement = (1.0-topJointConfidence);
        return topJointConfidence + maxJointImprovement*factor;
    }

    public static Double predictConfidenceAfterValueGain(DiscourseUnit DU, Double factor, String value,
                                                         Double topJointConfidence){
        assert 0.0 <= factor && 1.0 >= factor;
        String topJointHypothesis = DU.getHypothesisDistribution().getTopHypothesis();
        topJointConfidence = topJointConfidence == null ?
                DU.getHypothesisDistribution().get(topJointHypothesis) :
                topJointConfidence;
        Double maxJointImprovement = (1.0-topJointConfidence);
        for (String slot : DU.getHypotheses().get(topJointHypothesis).getAllSlotFillers().keySet()) {
            if (DU.getHypotheses().get(topJointHypothesis).getSlots().get(slot)!=null &&
                    DU.getHypotheses().get(topJointHypothesis).getSlots().get(slot).equals(value)) {
                StringDistribution marginal = DU.marginalSlotPathDistribution(slot);
                Double maxMarginalImprovement = (1.0 - marginal.get(value));
                topJointConfidence += maxJointImprovement*maxMarginalImprovement*factor;
            }
        }
        return topJointConfidence;
    }

    public static Double predictConfidenceAfterRoleGain(DiscourseUnit DU, Double factor, String role,
                                                        Double topJointConfidence){
        assert 0.0 <= factor && 1.0 >= factor;
        String topJointHypothesis = DU.getHypothesisDistribution().getTopHypothesis();
        topJointConfidence = topJointConfidence == null ?
                DU.getHypothesisDistribution().get(topJointHypothesis) :
                topJointConfidence;
        Double maxJointImprovement = (1.0-topJointConfidence);
        StringDistribution roleMarginal = DU.marginalSlotPathDistribution(role);
        String topValue = roleMarginal.getTopHypothesis();
        Double topOtherConfidence = 0.0;
        for (String otherSlot : DU.getAllSlotValuePairs().keySet()){
            if (otherSlot.equals(role))
                continue;
            topOtherConfidence = Double.max(topOtherConfidence,
                    DU.marginalSlotPathDistribution(otherSlot).get(topValue));
        }
        Double maxMarginalImprovement = topOtherConfidence;
        topJointConfidence += maxJointImprovement*maxMarginalImprovement*factor;
        return topJointConfidence;
    }

}
