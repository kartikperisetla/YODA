package edu.cmu.sv.dialog_management;

import edu.cmu.sv.dialog_management.dialog_act.DialogAct;
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

    public StringDistribution getHypothesisDistribution() {
        return hypothesisDistribution;
    }

    public void setHypothesisDistribution(StringDistribution hypothesisDistribution) {
        this.hypothesisDistribution = hypothesisDistribution;
    }

    public Map<String, SemanticsModel> getHypotheses() {
        return hypotheses;
    }

    public void setHypotheses(Map<String, SemanticsModel> hypotheses) {
        this.hypotheses = hypotheses;
    }

    public StringDistribution marginalSlotPathDistribution(String slotPath){
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


    //TODO: Need to implement actual discourse unit state tracking
    public void updateDiscourseUnit(Map<String, SemanticsModel> utterances,
                                  StringDistribution weights, Float timeStamp) {
        hypotheses = utterances;
        hypothesisDistribution = weights;
    }

}
