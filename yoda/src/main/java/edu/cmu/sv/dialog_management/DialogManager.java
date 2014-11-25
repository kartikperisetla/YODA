package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.system_action.dialog_task.DialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.system_action.dialog_act.*;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Combination;
import edu.cmu.sv.utils.HypothesisSetManagement;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Contains a dialog state tracker and specification of interfaces, etc.
 * Contains functions for assessing potential dialog moves.
 * Contains a main method which is the dialog agent loop.
 *
 */
public class DialogManager implements Runnable {
    private static Logger logger = Logger.getLogger("yoda.dialog_management.DialogManager");
    private static FileHandler fh;
    static {
        try {
            fh = new FileHandler("DialogManager.log");
            fh.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        logger.addHandler(fh);
    }

    YodaEnvironment yodaEnvironment;
    DiscourseUnit2 currentDialogState = null;

    public YodaEnvironment getYodaEnvironment() {
        return yodaEnvironment;
    }

    public void setYodaEnvironment(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    public DialogManager(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    /*
    * Select the best dialog act given all the possible classes and bindings
    * */
    private List<Pair<SystemAction, Double>> enumerateAndScorePossibleActions() {
        try {

            Map<SystemAction, Double> actionExpectedReward = new HashMap<>();

            //// add the null action
            actionExpectedReward.put(null, 0.0);

            //// Enumerate and evaluate sense clarification acts
            for (Class<? extends DialogAct> daClass : DialogRegistry.senseClarificationDialogActs) {
//            System.out.println("Enumerating and evaluating actions of class: "+daClass.getSimpleName());
                Map<String, Set<Object>> possibleBindingsPerVariable = new HashMap<>();
                Map<String, Class<? extends Thing>> parameters = daClass.newInstance().getClassParameters();
                Map<String, DiscourseUnit2.DialogStateHypothesis> dialogStateHypothesisMap = currentDialogState.getHypotheses();

                // Collect matches
                for (String dialogStateHypothesisID : dialogStateHypothesisMap.keySet()) {
                    for (String parameter : parameters.keySet()) {
                        if (!possibleBindingsPerVariable.containsKey(parameter))
                            possibleBindingsPerVariable.put(parameter, new HashSet<>());
                        SemanticsModel spokenByThem = dialogStateHypothesisMap.get(dialogStateHypothesisID).getSpokenByThem();
                        for (String path : spokenByThem.findAllPathsToClass(parameters.get(parameter).getSimpleName())) {
                            possibleBindingsPerVariable.get(parameter).add(spokenByThem.newGetSlotPathFiller(path));
                        }
                    }
                }

                // create an action and evaluate reward for each possible binding
                for (Map<String, Object> binding : Combination.possibleBindings(possibleBindingsPerVariable)) {
                    DialogAct dialogAct = daClass.newInstance();
                    dialogAct.bindVariables(binding);
                    Double expectedReward = dialogAct.reward(currentDialogState);
                    actionExpectedReward.put(dialogAct, expectedReward);
                }

            }

            //// Get the expected rewards from slot-filling dialog acts,
            for (String hypothesisID : currentDialogState.getHypotheses().keySet()) {
                DiscourseUnit2.DialogStateHypothesis dsHypothesis = currentDialogState.getHypotheses().get(hypothesisID);
                SemanticsModel hypothesis = dsHypothesis.getSpokenByThem();
                Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                        get(hypothesis.getSlotPathFiller("dialogAct"));
                // add contribution from dialog tasks
                if (DialogRegistry.dialogTaskRegistry.containsKey(daClass)) {
                    for (Class<? extends DialogTask> taskClass : DialogRegistry.dialogTaskRegistry.get(daClass)) {
                        DialogTask task = taskClass.getDeclaredConstructor(Database.class).newInstance(yodaEnvironment.db);
                        task.setTaskSpec(hypothesis.deepCopy());
                        Collection<DialogAct> slotFillingDialogActs = task.enumerateAndEvaluateSlotFillingActions();
                        for (DialogAct slotFillingDialogAct : slotFillingDialogActs){
                            Double expectedReward = RewardAndCostCalculator.
                                    executabilityRewardGain(task, 1.0/slotFillingDialogActs.size()) *
                                    currentDialogState.getHypothesisDistribution().get(hypothesisID);
                            actionExpectedReward.put(slotFillingDialogAct, expectedReward);
                        }
                    }
                }
            }
            //// Get expected rewards for executing dialog and non-dialog tasks
            for (String hypothesisID : currentDialogState.getHypotheses().keySet()) {
                DiscourseUnit2.DialogStateHypothesis dsHypothesis = currentDialogState.getHypotheses().get(hypothesisID);
                SemanticsModel hypothesis = dsHypothesis.getSpokenByThem();
                Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                        get(hypothesis.getSlotPathFiller("dialogAct"));

                // add contribution from dialog tasks
                if (DialogRegistry.dialogTaskRegistry.containsKey(daClass)) {
                    for (Class<? extends DialogTask> taskClass : DialogRegistry.dialogTaskRegistry.get(daClass)) {
                        DialogTask task = taskClass.getDeclaredConstructor(Database.class).newInstance(yodaEnvironment.db);
                        task.setTaskSpec(hypothesis.deepCopy());
                        Double expectedReward = RewardAndCostCalculator.dialogTaskReward(currentDialogState, task);
                        actionExpectedReward.put(task, expectedReward);
                    }
                }
                // add contribution from non dialog tasks
                if (DialogRegistry.nonDialogTaskRegistry.containsKey(daClass)) {
                    for (Class<? extends NonDialogTask> taskClass : DialogRegistry.nonDialogTaskRegistry.get(daClass)) {
                        NonDialogTask task = taskClass.getDeclaredConstructor(Database.class).newInstance(yodaEnvironment.db);
                        task.setTaskSpec(hypothesis.deepCopy());
                        Double expectedReward = RewardAndCostCalculator.nonDialogTaskReward(currentDialogState, task);
                        actionExpectedReward.put(task, expectedReward);
                    }
                }
            }


            return HypothesisSetManagement.keepNBestBeam(actionExpectedReward, 10000);
        } catch (IllegalAccessException | InstantiationException |  NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    @Override
    public void run() {
        while (true){
            try {
                DiscourseUnit2 DmInput = yodaEnvironment.DmInputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (DmInput!=null) {
                    currentDialogState = DmInput;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
            List<Pair<SystemAction, Double>> rankedActions = enumerateAndScorePossibleActions();
            logger.info("Ranked actions: " + rankedActions.toString());
            SystemAction selectedAction = rankedActions.get(0).getKey();
            if (selectedAction!=null)
                yodaEnvironment.nlg.speak(((DialogAct)selectedAction).getNlgCommand(), Grammar.DEFAULT_GRAMMAR_PREFERENCES);
        }
    }
}
