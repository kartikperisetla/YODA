package edu.cmu.sv.dialog_management;

import edu.cmu.sv.dialog_act.DialogAct;
import edu.cmu.sv.dialog_state.DialogStateTracker;
import edu.cmu.sv.dialog_state.DiscourseUnit;
import edu.cmu.sv.utils.Combination;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Contains a dialog state tracker and specification of interfaces, etc.
 * Contains functions for assessing potential dialog moves.
 * Contains a main method which is the dialog agent loop.
 *
 */
public class DialogManager {
    private DialogStateTracker tracker;


    public void evaluateActions(){
        Set<DiscourseUnit> discourseUnits = tracker.getDiscourseUnits();


        for (DiscourseUnit DU : discourseUnits) {
            // 1) collect roles and values across this DU
            Map<String, Set<String>> roleValuePairs = DU.getAllSlotValuePairs();
            // 1-a) determine the set of values
            Set<String> values = new HashSet<>();
            for (String role : roleValuePairs.keySet()){
                values.addAll(roleValuePairs.get(role));
            }

            // 2) create a dialog act descriptor for each possible dialog act
            Map<DialogAct.DA_TYPE, Set<Map<String, String>>> descriptors = new HashMap<>();
            for (DialogAct.DA_TYPE daType : DialogAct.dialogActContentSpec.keySet()){
                Map<String, String> parameters = DialogAct.dialogActContentSpec.get(daType);
                Map updatedParameters = new HashMap<>();
                for (String key : parameters.keySet()){
                    if (parameters.get(key)=="value")
                        updatedParameters.put(key, values);
                    else if (parameters.get(key)=="role")
                        updatedParameters.put(key, roleValuePairs.keySet());
                    else
                        throw new Error("unsupported parameter type for dialog act descriptor");
                }
                descriptors.put(daType, Combination.possibleBindings(updatedParameters));
            }

            // 3) for each dialog act descriptor, evaluate expected reward

            // select the best dialog act descriptor, add it to the DU's it belongs to
        }
    }

}
