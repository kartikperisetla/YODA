package edu.cmu.sv.dialog_management;

import edu.cmu.sv.action.Action;
import edu.cmu.sv.action.dialog_act.*;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.action.dialog_task.DialogTask;
import edu.cmu.sv.action.non_dialog_task.NonDialogTask;
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
    public List<Pair<Action, Double>> selectAction() throws IllegalAccessException, InstantiationException {
        DiscourseUnit DU = tracker.getDiscourseUnit();
        // 1) collect roles and values across this DU
        Map<String, Set<String>> roleValuePairs = DU.getAllSlotValuePairs();
        // 1-a) determine the set of values
        Set<String> values = new HashSet<>();
        for (String role : roleValuePairs.keySet()) {
            values.addAll(roleValuePairs.get(role));
        }

        Map<Action, Double> actionExpectedReward = new HashMap<>();

        //// Get reward for clarification acts
        for (Class <? extends DialogAct> cls : DialogRegistry.clarificationDialogActs) {
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
                DialogAct clarificationDialogAct = cls.newInstance();
                clarificationDialogAct = clarificationDialogAct.bindVariables(binding);
                possibleDialogActs.add(clarificationDialogAct);
            }

            // 3) for each dialog act descriptor, evaluate expected reward
            for (DialogAct dialogAct : possibleDialogActs) {
                Double expectedReward = dialogAct.reward(DU);
                Double expectedCost = dialogAct.cost(DU);
                actionExpectedReward.put(dialogAct, expectedReward - expectedCost);
            }
        }

        //// Get expected rewards from dialog and non-dialog tasks
        for (String hypothesisID : DU.getHypotheses().keySet()) {
            SemanticsModel hypothesis = DU.getHypotheses().get(hypothesisID);
            Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                    get(hypothesis.getSlotPathFiller("dialogAct"));

            // add contribution from dialog tasks
            if (DialogRegistry.dialogTaskRegistry.containsKey(daClass)) {
                for (Class<? extends DialogTask> taskClass : DialogRegistry.dialogTaskRegistry.get(daClass)) {
                    DialogTask task = taskClass.newInstance();
                    task.setTaskSpec(hypothesis.deepCopy());
                    Double expectedReward = RewardAndCostCalculator.dialogTaskReward(DU, task);
                    Double cost =
                    actionExpectedReward.put(task, expectedReward);
                }
            }
            // add contribution from non dialog tasks
            if (DialogRegistry.nonDialogTaskRegistry.containsKey(daClass)) {
                for (Class<? extends NonDialogTask> taskClass : DialogRegistry.nonDialogTaskRegistry.get(daClass)) {
                    NonDialogTask task = taskClass.newInstance();
                    task.setTaskSpec(hypothesis.deepCopy());
                    Double expectedReward = RewardAndCostCalculator.nonDialogTaskReward(DU, task);
                    actionExpectedReward.put(task, expectedReward);
                }
            }
        }

        return NBest.keepBeam(actionExpectedReward, 100);
    }



}
