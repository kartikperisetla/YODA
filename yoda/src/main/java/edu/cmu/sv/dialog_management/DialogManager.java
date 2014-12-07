package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.dialog_task.ActionEnumeration;
import edu.cmu.sv.dialog_state_tracking.DialogStateHypothesis;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ClarificationDialogAct;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.system_action.dialog_act.*;

import edu.cmu.sv.utils.HypothesisSetManagement;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
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
    StringDistribution dialogStateDistribution = new StringDistribution();
    Map<String, DialogStateHypothesis> dialogStateHypotheses = new HashMap<>();

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
            actionExpectedReward.put(null, RewardAndCostCalculator.penaltyForSpeaking);

            // enumerate and evaluate actions that can be evaluated by summing marginals across the dialog state distribution
            for (String dialogStateHypothesisId : dialogStateHypotheses.keySet()) {
                DialogStateHypothesis currentDialogStateHypothesis = dialogStateHypotheses.get(dialogStateHypothesisId);
                for (Class<? extends DialogAct> dialogActClass : DialogRegistry.argumentationDialogActs) {
                    DialogAct dialogActInstance = dialogActClass.newInstance();
                    Set<Map<String, Object>> possibleBindings = ActionEnumeration.
                            getPossibleBindings(dialogActInstance, yodaEnvironment);
                    for (Map<String, Object> binding : possibleBindings) {
                        for (String discourseUnitHypothesisId : currentDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                                keySet()) {
                            DiscourseUnitHypothesis contextDiscourseUnitHypothesis = currentDialogStateHypothesis.
                                    getDiscourseUnitHypothesisMap().get(discourseUnitHypothesisId);
                            DialogAct newDialogActInstance = dialogActClass.newInstance();
                            newDialogActInstance.bindVariables(binding);
                            Double currentReward = newDialogActInstance.reward(
                                    currentDialogStateHypothesis, contextDiscourseUnitHypothesis) *
                                    dialogStateDistribution.get(dialogStateHypothesisId);
                            accumulateReward(actionExpectedReward, newDialogActInstance, currentReward);
                        }
                    }
                }
            }

            // enumerate and evaluate clarification actions
            for (Class<? extends ClarificationDialogAct> dialogActClass : DialogRegistry.clarificationDialogActs) {
                ClarificationDialogAct dialogActInstance = dialogActClass.newInstance();
                Set<Map<String, Object>> possibleBindings = ActionEnumeration.
                        getPossibleBindings(dialogActInstance, yodaEnvironment);
                for (Map<String, Object> binding : possibleBindings) {
                    ClarificationDialogAct newDialogActInstance = dialogActClass.newInstance();
                    newDialogActInstance.bindVariables(binding);
                    Double currentReward = newDialogActInstance.reward(dialogStateDistribution, dialogStateHypotheses);
                    accumulateReward(actionExpectedReward, newDialogActInstance, currentReward);
                }
            }


            //todo: enumerate and evaluate actions that require multiple DU hypotheses to be enumerated (ex: disambiguation)


            /*
            //// Get expected rewards for executing non-dialog tasks
            for (String hypothesisID : currentDialogState.getHypotheses().keySet()) {
                DiscourseUnitHypothesis.DiscourseUnitHypothesis dsHypothesis = currentDialogState.getHypotheses().get(hypothesisID);
                SemanticsModel hypothesis = dsHypothesis.getSpokenByThem();
                Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
                        get(hypothesis.getSlotPathFiller("dialogAct"));
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
            */

            return HypothesisSetManagement.keepNBestBeam(actionExpectedReward, 10000);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    private void accumulateReward(Map<SystemAction, Double> actionExpectedReward, DialogAct dialogAct, Double currentReward){
        boolean alreadyFound = false;
        for (SystemAction key : actionExpectedReward.keySet()){
            if (key==null)
                continue;
            if (key.evaluationMatch(dialogAct)){
                alreadyFound = true;
                actionExpectedReward.put(key, actionExpectedReward.get(key) + currentReward);
                break;
            }
        }
        if (!alreadyFound){
            actionExpectedReward.put(dialogAct, currentReward);
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                Pair<Map<String, DialogStateHypothesis>, StringDistribution> DmInput = null;
                // empty out the queue to get the most recent dialog state
                while (true) {
                    Pair<Map<String, DialogStateHypothesis>, StringDistribution> tmp;
                    tmp = yodaEnvironment.DmInputQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (tmp==null)
                        break;
                    else
                        DmInput = tmp;
                }
                if (DmInput!=null) {
                    dialogStateHypotheses = DmInput.getLeft();
                    dialogStateDistribution = DmInput.getRight();
                }
                List<Pair<SystemAction, Double>> rankedActions = enumerateAndScorePossibleActions();
                logger.info("Ranked actions: " + rankedActions.toString());
                SystemAction selectedAction = rankedActions.get(0).getKey();
                if (selectedAction!=null)
                    yodaEnvironment.nlg.speak(((DialogAct)selectedAction).getNlgCommand(), Grammar.DEFAULT_GRAMMAR_PREFERENCES);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
