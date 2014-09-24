package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.system_action.dialog_act.*;
import edu.cmu.sv.dialog_state_tracking.DialogStateTracker;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_task.DialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.utils.Combination;
import edu.cmu.sv.utils.HypothesisSetManagement;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

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
    private Database db;

    public DialogManager() {
        tracker = new DialogStateTracker();
        db = new Database();
    }

    public DialogStateTracker getTracker() {
        return tracker;
    }


    /*
    * Select the best dialog act given all the possible classes and bindings
    *
    * */
    public List<Pair<SystemAction, Double>> selectAction() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        DiscourseUnit DU = tracker.getDiscourseUnit();
        // 1) collect roles and values across this DU
        Map<String, Set<String>> roleValuePairs = DU.getAllNonSpecialSlotValueLeafPairs();
        // 1-a) determine the set of values
        Set<String> possibleValueBindings = new HashSet<>();
        for (String role : roleValuePairs.keySet()) {
            possibleValueBindings.addAll(roleValuePairs.get(role));
        }
        Set<String> possibleRoleBindings = roleValuePairs.keySet().stream().
                filter(x -> possibleValueBindings.containsAll(roleValuePairs.get(x))).
                collect(Collectors.toSet());

        Map<SystemAction, Double> actionExpectedReward = new HashMap<>();

        //// Get reward for clarification acts
        for (Class <? extends DialogAct> cls : DialogRegistry.clarificationDialogActs) {
            // 2) create a dialog act instance for each possible dialog act
            Set<DialogAct> possibleDialogActs = new HashSet<>();
            Map<String, String> parameters = cls.newInstance().getParameters();

//                System.out.println("parameters:"+parameters);
            Map<String, Set<String>> updatedParameters = new HashMap<>();
            for (String key : parameters.keySet()) {
                if (parameters.get(key).equals("value"))
                    updatedParameters.put(key, possibleValueBindings);
                else if (parameters.get(key).equals("role"))
                    updatedParameters.put(key, possibleRoleBindings);
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

        //// Get the expected rewards from slot-filling dialog acts,
        for (String hypothesisID : DU.getHypotheses().keySet()) {
            SemanticsModel hypothesis = DU.getHypotheses().get(hypothesisID);
            Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                    get(hypothesis.getSlotPathFiller("dialogAct"));
            // add contribution from dialog tasks
            if (DialogRegistry.dialogTaskRegistry.containsKey(daClass)) {
                for (Class<? extends DialogTask> taskClass : DialogRegistry.dialogTaskRegistry.get(daClass)) {
                    DialogTask task = taskClass.getDeclaredConstructor(Database.class).newInstance(db);
                    task.setTaskSpec(hypothesis.deepCopy());
                    Collection<DialogAct> slotFillingDialogActs = task.enumerateAndEvaluateSlotFillingActions();
                    for (DialogAct slotFillingDialogAct : slotFillingDialogActs){
                        Double expectedReward = RewardAndCostCalculator.
                                executabilityRewardGain(task, 1.0/slotFillingDialogActs.size()) *
                                DU.getHypothesisDistribution().get(hypothesisID);
                        HypothesisSetManagement.putOrIncrement(actionExpectedReward, slotFillingDialogAct, expectedReward);
                    }
                }
            }
        }

        //// Get expected rewards for executing dialog and non-dialog tasks
        for (String hypothesisID : DU.getHypotheses().keySet()) {
            SemanticsModel hypothesis = DU.getHypotheses().get(hypothesisID);
            Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                    get(hypothesis.getSlotPathFiller("dialogAct"));

            // add contribution from dialog tasks
            if (DialogRegistry.dialogTaskRegistry.containsKey(daClass)) {
                for (Class<? extends DialogTask> taskClass : DialogRegistry.dialogTaskRegistry.get(daClass)) {
                    DialogTask task = taskClass.getDeclaredConstructor(Database.class).newInstance(db);
                    task.setTaskSpec(hypothesis.deepCopy());
                    Double expectedReward = RewardAndCostCalculator.dialogTaskReward(DU, task);
                    HypothesisSetManagement.putOrIncrement(actionExpectedReward, task, expectedReward);
                }
            }
            // add contribution from non dialog tasks
            if (DialogRegistry.nonDialogTaskRegistry.containsKey(daClass)) {
                for (Class<? extends NonDialogTask> taskClass : DialogRegistry.nonDialogTaskRegistry.get(daClass)) {
                    NonDialogTask task = taskClass.getDeclaredConstructor(Database.class).newInstance(db);
                    task.setTaskSpec(hypothesis.deepCopy());
                    Double expectedReward = RewardAndCostCalculator.nonDialogTaskReward(DU, task);
                    HypothesisSetManagement.putOrIncrement(actionExpectedReward, task, expectedReward);
                }
            }
        }

        return HypothesisSetManagement.keepNBestBeam(actionExpectedReward, 10000);
    }



}
