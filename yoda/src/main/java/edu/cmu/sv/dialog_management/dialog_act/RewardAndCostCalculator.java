package edu.cmu.sv.dialog_management.dialog_act;

import edu.cmu.sv.dialog_management.DiscourseUnit;
import edu.cmu.sv.utils.StringDistribution;

/**
 * Created by David Cohen on 9/8/14.
 *
 * Contains standard functions that are used to compute reward and utility across dialog acts
 *
 */
public class RewardAndCostCalculator {

    public static Double nonDialogTaskReward(DiscourseUnit DU){return null;}

    public static Double dialogTaskReward(DiscourseUnit DU){return null;}

    public static Double slotFillingDialogActReward(DiscourseUnit DU){return null;}

    /*
    * To compute the reward for a clarification dialog act,
    * take a weighted sum of the rewards for
    *
    * */
    public static Double clarificationDialogActReward(DiscourseUnit DU, Double predictedConfidence){
        return null;
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
