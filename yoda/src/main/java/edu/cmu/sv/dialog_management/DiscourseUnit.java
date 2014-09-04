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
            Map<String, Set<String>> modelSlotValues = semanticsModel.getAllSlotFillers();
            for (String key : modelSlotValues.keySet()){
                if (!ans.containsKey(key))
                    ans.put(key, new HashSet<>());
                ans.get(key).addAll(modelSlotValues.get(key));
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
                StringDistribution summedMarginalsAcrossAllRoles = new StringDistribution();
                for (String slot : hypotheses.get(topJointHypothesis).getAllSlotFillers().keySet()){
                    StringDistribution marginal = marginalSlotPathDistribution(slot);
                    for (String key : marginal.keySet()){
                        summedMarginalsAcrossAllRoles.extend(key, marginal.get(key));
                    }
                }
                Optional<Double> maxSummedMarginalValueWeight = summedMarginalsAcrossAllRoles.keySet().stream().
                        filter((x) -> hypotheses.get(topJointHypothesis).getAllSlotFillers().get(role).contains(x)).
                        filter((x) -> x != null).
                        map(summedMarginalsAcrossAllRoles::get).
                        max(Comparator.comparingDouble((x) -> x));
                Double maxMarginalImprovement = 1.0 - (maxSummedMarginalValueWeight.isPresent() ?
                        maxSummedMarginalValueWeight.get() : 1.0);
                topJointConfidence += Double.max(maxMarginalImprovement, 0.0) * maxJointImprovement *
                        DialogAct.expectedRoleConfidenceGain.get(dialogActDescriptor.getKey());
            }
        }
        return topJointConfidence;
    }

    //TODO: Need to implement actual discourse unit state tracking
    public void updateDiscourseUnit(Map<String, SemanticsModel> utterances,
                                  StringDistribution weights, Float timeStamp) {
        hypotheses = utterances;
        hypothesisDistribution = weights;
    }

}
