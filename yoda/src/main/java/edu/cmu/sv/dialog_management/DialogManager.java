package edu.cmu.sv.dialog_management;

import edu.cmu.sv.dialog_management.dialog_act.*;
import edu.cmu.sv.utils.Combination;
import edu.cmu.sv.utils.NBest;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

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

    public DialogManager() {
        tracker = new DialogStateTracker();
    }

    public DialogStateTracker getTracker() {
        return tracker;
    }


    /*
    * Select the best dialog act given all the possible classes and bindings
    *
    * */
    public List<Pair<DialogAct, Double>> selectDialogAct() throws IllegalAccessException, InstantiationException {
        DiscourseUnit DU = tracker.getDiscourseUnit();
        // 1) collect roles and values across this DU
        Map<String, Set<String>> roleValuePairs = DU.getAllSlotValuePairs();
        // 1-a) determine the set of values
        Set<String> values = new HashSet<>();
        for (String role : roleValuePairs.keySet()) {
            values.addAll(roleValuePairs.get(role));
        }

        Map<DialogAct, Double> descriptorExpectedReward = new HashMap<>();
        for (Class <? extends DialogAct> cls : DialogRegistry.systemOutputDialogActs) {
            // 2) create a dialog act instance for each possible dialog act
            Set<DialogAct> possibleDialogActs = new HashSet<>();
            Map<String, String> parameters = cls.newInstance().getParameters();

//                System.out.println("parameters:"+parameters);
            Map<String, Set<String>> updatedParameters = new HashMap<>();
            for (String key : parameters.keySet()) {
                if (parameters.get(key).equals("value"))
                    updatedParameters.put(key, values);
                else if (parameters.get(key).equals("role"))
                    updatedParameters.put(key, roleValuePairs.keySet());
                else
                    throw new Error("unsupported parameter type for dialog act descriptor");
            }
            for (Map<String, String> binding : Combination.possibleBindings(updatedParameters)){
                // don't allow multiple parameters to have the same value
                if (binding.values().size()!=new HashSet<>(binding.values()).size())
                    continue;
                DialogAct dum = cls.newInstance();
                dum = dum.bindVariables(binding);
                possibleDialogActs.add(dum);
            }

            // 3) for each dialog act descriptor, evaluate expected reward
            StringDistribution dialogActDistribution = DU.marginalSlotPathDistribution("dialogAct");
            for (DialogAct dialogAct : possibleDialogActs) {
                Double expectedReward = dialogAct.reward(DU);
                Double expectedCost = dialogAct.cost(DU);
                descriptorExpectedReward.put(dialogAct, expectedReward - expectedCost);
            }

        }

//        System.out.println("DialogManager.selectAction: descriptorExpectedReward:");
//        for (DialogAct dialogAct : descriptorExpectedReward.keySet()){
//            System.out.println(descriptorExpectedReward.get(dialogAct));
//            System.out.println(dialogAct);
//        }

        return NBest.keepBeam(descriptorExpectedReward, 5);
    }

//
//    Double dialogActTaskExpectedReward(Double confidence, StringDistribution dialogActDistribution, boolean takeTurn){
//        Double expectedReward = 0.0;
//        for (String dialogAct : dialogActDistribution.keySet()){
//            DialogTaskPreferences preferences = dialogActTaskRegistry.get(dialogAct).getPreferences();
//            expectedReward += dialogActDistribution.get(dialogAct)*
//                    ((preferences.rewardForCorrectExecution * confidence) -
//                            (takeTurn ? (preferences.penaltyForDelay * confidence) : 0) -
//                            (preferences.penaltyForIncorrectExecution * (1 - confidence)));
//        }
//        return expectedReward;
//    }

}
