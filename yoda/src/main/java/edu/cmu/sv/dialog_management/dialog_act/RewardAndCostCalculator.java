package edu.cmu.sv.dialog_management.dialog_act;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.dialog_management.DiscourseUnit;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.task_interface.dialog_task.DialogTask;
import edu.cmu.sv.utils.StringDistribution;

import java.util.Set;

/**
 * Created by David Cohen on 9/8/14.
 *
 * Contains standard functions that are used to compute reward and utility for possible system actions
 *
 */
public class RewardAndCostCalculator {

    public static Double nonDialogTaskReward(DiscourseUnit DU){return null;}

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

    public static Double slotFillingDialogActReward(DiscourseUnit DU){return null;}

    /*
    * To compute the reward for a clarification dialog act,
    * incorporate all the dialog task rewards and non dialog task rewards,
    * weighed by the presence of relevant dialog acts in the DU hypotheses n-best list
    *
    * */
    public static Double clarificationDialogActReward(DiscourseUnit DU, Double predictedConfidence)
            throws IllegalAccessException, InstantiationException {
        Double totalReward = 0.0;
        StringDistribution predictedDistribution = DU.getHypothesisDistribution().deepCopy();
        predictedDistribution.setAndNormalize(predictedDistribution.getTopHypothesis(), predictedConfidence);

        for (String hypothesisID : DU.getHypotheses().keySet()){
            SemanticsModel hypothesis = DU.getHypotheses().get(hypothesisID);
            Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                    get(hypothesis.getSlotPathFiller("dialogAct"));
            for (Class<? extends DialogTask> taskClass : DialogRegistry.dialogTaskRegistry.get(daClass)){
                DialogTask dialogTask = taskClass.newInstance();
                dialogTask.setTaskSpec(hypothesis.deepCopy());
                Double probabilityCorrect = 
            }
        }



        StringDistribution currentDialogActMarginal = DU.marginalSlotPathDistribution("dialogAct");
        for (String dialogAct : currentDialogActMarginal.keySet()){
            Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.get(dialogAct);
            for (Class<? extends DialogTask> dialogTask : DialogRegistry.dialogTaskRegistry.get(daClass)){
                totalReward += currentDialogActMarginal.get(dialogAct)*0;
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
