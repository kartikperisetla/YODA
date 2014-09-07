package edu.cmu.sv.dialog_management;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by David Cohen on 9/2/14.
 */
public class DiscourseUnit {
    // Represent joint hypotheses about the discourse unit

    StringDistribution hypothesisDistribution;
    Map<String, SemanticsModel> hypotheses;

    StringDistribution marginalSlotPathDistribution(String slotPath){
        StringDistribution ans = new StringDistribution();
        for (String hypothesis : hypotheses.keySet()){
            ans.extend(hypotheses.get(hypothesis).getSlotPathFiller(slotPath), hypothesisDistribution.get(hypothesis));
        }
        return ans;
    }


    /*
    Collect all the slot-value pairs that occur in this Discourse Unit
    * */
    public Map<String, Set<String>> getAllSlotValuePairs(){
        Map<String, Set<String>> ans = new HashMap<>();
        for (SemanticsModel semanticsModel : hypotheses.values()){
            Map<String, String> modelSlotValues = semanticsModel.getAllSlotFillers();
            for (String key : modelSlotValues.keySet()){
                if (!ans.containsKey(key))
                    ans.put(key, new HashSet<>());
                ans.get(key).add(modelSlotValues.get(key));
            }
        }
        return ans;
    }

    /*
    * Predict the probability of the new top joint hypothesis after a clarification dialog act is performed
    * This is based off of three heuristics for the three different types of clarification:
    *  - request joint, value-specific, role-specific
    *
    * The more roles / values parametrize a dialog act descriptor, the higher the predicted confidence
    *
    * */
    public Double predictJointConfidenceAfterClarification(Pair<DialogAct.DA_TYPE, Map<String, String>> dialogActDescriptor){
        String topJointHypothesis = hypothesisDistribution.getTopHypothesis();
        Double topJointConfidence = hypothesisDistribution.get(topJointHypothesis);
        Double maxJointImprovement = (1.0-topJointConfidence);
        if (DialogAct.expectedJointConfidenceGain.containsKey(dialogActDescriptor.getKey())){
            return topJointConfidence + maxJointImprovement*DialogAct.expectedJointConfidenceGain.get(dialogActDescriptor.getKey());
        }
        if (DialogAct.expectedValueConfidenceGain.containsKey(dialogActDescriptor.getKey())){
            for (String value : dialogActDescriptor.getValue().values()) {
                for (String slot : hypotheses.get(topJointHypothesis).getAllSlotFillers().keySet()) {
                    if (hypotheses.get(topJointHypothesis).getSlots().get(slot).equals(value)) {
                        StringDistribution marginal = marginalSlotPathDistribution(slot);
                        Double maxMarginalImprovement = (1.0 - marginal.get(value));
                        topJointConfidence += maxJointImprovement*maxMarginalImprovement*
                                DialogAct.expectedValueConfidenceGain.get(dialogActDescriptor.getKey());
                    }
                }
            }
        } else if (DialogAct.expectedRoleConfidenceGain.containsKey(dialogActDescriptor.getKey())){
            for (String role : dialogActDescriptor.getValue().values()) {
                StringDistribution roleMarginal = marginalSlotPathDistribution(role);
                String topValue = roleMarginal.getTopHypothesis();
                Double currentConfidence = roleMarginal.get(topValue);
//                System.out.println("DU.predictJoint: currentConfidence:"+currentConfidence);
                Double topOtherConfidence = 0.0;
                for (String otherSlot : getAllSlotValuePairs().keySet()){
                    if (otherSlot.equals(role))
                        continue;
                    topOtherConfidence = Double.max(topOtherConfidence,
                            marginalSlotPathDistribution(otherSlot).get(topValue));
//                    System.out.println("DU.predictJoint: topOtherConfidence:"+topOtherConfidence);
                }
                Double maxMarginalImprovement = topOtherConfidence;
//                System.out.println("DU.predictJoint: maxMarginalImprovement:"+maxMarginalImprovement);
                topJointConfidence += maxJointImprovement*maxMarginalImprovement*
                        DialogAct.expectedRoleConfidenceGain.get(dialogActDescriptor.getKey());
            }
        }
//        System.out.println("DiscourseUnit.predictJointConfidenceAfterClarification: dialog act type:"+
//                dialogActDescriptor.getLeft());
//        System.out.println("DiscourseUnit.predictJointConfidenceAfterClarification: predicted confidence:"+
//                topJointConfidence);
        return topJointConfidence;
    }

    //TODO: Need to implement actual discourse unit state tracking
    public void updateDiscourseUnit(Map<String, SemanticsModel> utterances,
                                  StringDistribution weights, Float timeStamp) {
        hypotheses = utterances;
        hypothesisDistribution = weights;
    }

}
