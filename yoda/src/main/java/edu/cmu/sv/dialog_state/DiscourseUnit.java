package edu.cmu.sv.dialog_state;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

}
