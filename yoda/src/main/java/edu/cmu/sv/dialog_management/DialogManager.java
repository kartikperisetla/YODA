package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.dialog_task.ActionEnumeration;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ClarificationDialogAct;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.utils.Combination;
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
import java.util.stream.Collectors;

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
    Map<String, DialogState> dialogStateHypotheses = new HashMap<>();

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
            actionExpectedReward.put(null,
                    RewardAndCostCalculator.penaltyForSpeaking +
                            RewardAndCostCalculator.outstandingGroundingRequest(dialogStateDistribution, dialogStateHypotheses, "user") *
                                    RewardAndCostCalculator.penaltyForSpeakingOutOfTurn);

            // enumerate and evaluate actions that can be evaluated by summing marginals across the dialog state distribution
            for (String dialogStateHypothesisId : dialogStateHypotheses.keySet()) {
                DialogState currentDialogState = dialogStateHypotheses.get(dialogStateHypothesisId);
                for (Class<? extends DialogAct> dialogActClass : DialogRegistry.argumentationDialogActs) {
                    DialogAct dialogActInstance = dialogActClass.newInstance();
                    Set<Map<String, Object>> possibleBindings = ActionEnumeration.
                            getPossibleIndividualBindings(dialogActInstance, yodaEnvironment, ActionEnumeration.FOCUS_CONSTRAINT.IN_FOCUS);
                    for (Map<String, Object> binding : possibleBindings) {
                        for (String discourseUnitHypothesisId : currentDialogState.getDiscourseUnitHypothesisMap().
                                keySet()) {
                            DiscourseUnit contextDiscourseUnit = currentDialogState.
                                    getDiscourseUnitHypothesisMap().get(discourseUnitHypothesisId);
                            DialogAct newDialogActInstance = dialogActClass.newInstance();
                            newDialogActInstance.bindVariables(binding);
                            Double currentReward = newDialogActInstance.reward(
                                    currentDialogState, contextDiscourseUnit) *
                                    dialogStateDistribution.get(dialogStateHypothesisId);
                            accumulateReward(actionExpectedReward, newDialogActInstance, currentReward);
                        }
                    }
                }

                //// slot-filling dialog acts
                for (Class<? extends DialogAct> dialogActClass : DialogRegistry.slotFillingDialogActs){
                    DialogAct dialogActInstance = dialogActClass.newInstance();
                    for (String discourseUnitHypothesisId : currentDialogState.getDiscourseUnitHypothesisMap().
                            keySet()) {
                        DiscourseUnit contextDiscourseUnit = currentDialogState.
                                getDiscourseUnitHypothesisMap().get(discourseUnitHypothesisId);
//                        ActionEnumeration.getPossibleNonIndividualBindings(dialogActInstance, contextDiscourseUnit).forEach(System.out::println);
                        for (Map<String, Object> binding : ActionEnumeration.getPossibleNonIndividualBindings(
                                dialogActInstance, contextDiscourseUnit)){
                            DialogAct newDialogActInstance = dialogActClass.newInstance();
                            newDialogActInstance.bindVariables(binding);
                            Double currentReward = newDialogActInstance.reward(currentDialogState, contextDiscourseUnit);
                            accumulateReward(actionExpectedReward, newDialogActInstance, currentReward);
                        }

                    }
                }
            }

            // enumerate and evaluate clarification actions
            for (Class<? extends ClarificationDialogAct> dialogActClass : DialogRegistry.clarificationDialogActs) {
                ClarificationDialogAct dialogActInstance = dialogActClass.newInstance();
                Set<Map<String, Object>> possibleBindings = ActionEnumeration.
                        getPossibleIndividualBindings(dialogActInstance, yodaEnvironment, ActionEnumeration.FOCUS_CONSTRAINT.IN_FOCUS);
                for (Map<String, Object> binding : possibleBindings) {
                    ClarificationDialogAct newDialogActInstance = dialogActClass.newInstance();
                    newDialogActInstance.bindVariables(binding);
                    Double currentReward = newDialogActInstance.clarificationReward(dialogStateDistribution, dialogStateHypotheses);
                    accumulateReward(actionExpectedReward, newDialogActInstance, currentReward);
                }
            }

            // enumerate non-dialog tasks
            Set<NonDialogTask> enumeratedNonDialogTasks = new HashSet<>();
            for (ActionSchema actionSchema : DialogRegistry.actionSchemata) {
                for (String dialogStateHypothesisId : dialogStateHypotheses.keySet()) {
                    DialogState currentDialogState = dialogStateHypotheses.get(dialogStateHypothesisId);
                    for (String discourseUnitHypothesisId : currentDialogState.getDiscourseUnitHypothesisMap().
                            keySet()) {
                        DiscourseUnit contextDiscourseUnit = currentDialogState.
                                getDiscourseUnitHypothesisMap().get(discourseUnitHypothesisId);
                        for (NonDialogTask localEnumeratedTask : contextDiscourseUnit.actionAnalysis.enumeratedNonDialogTasks){
                            boolean alreadyFound = false;
                            for (NonDialogTask existingTask : enumeratedNonDialogTasks) {
                                if (localEnumeratedTask.evaluationMatch(existingTask)) {
                                    alreadyFound = true;
                                    break;
                                }
                            }
                            if (!alreadyFound){
                                enumeratedNonDialogTasks.add(localEnumeratedTask);
                            }

                        }
                    }
                }
            }

            // evaluate non-dialog tasks
            for (NonDialogTask task : enumeratedNonDialogTasks){
                Double currentReward = RewardAndCostCalculator.nonDialogTaskReward(task, dialogStateHypotheses, dialogStateDistribution);
                accumulateReward(actionExpectedReward, task, currentReward);
            }

            return HypothesisSetManagement.keepNBestBeam(actionExpectedReward, 10000);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    private void accumulateReward(Map<SystemAction, Double> actionExpectedReward, SystemAction systemAction, Double currentReward){
        boolean alreadyFound = false;
        for (SystemAction key : actionExpectedReward.keySet()){
            if (key==null)
                continue;
            if (key.evaluationMatch(systemAction)){
                alreadyFound = true;
                actionExpectedReward.put(key, actionExpectedReward.get(key) + currentReward);
                break;
            }
        }
        if (!alreadyFound){
            actionExpectedReward.put(systemAction, currentReward);
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                Pair<Map<String, DialogState>, StringDistribution> DmInput = null;
                // empty out the queue to get the most recent dialog state
                while (true) {
                    Pair<Map<String, DialogState>, StringDistribution> tmp;
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
                if (selectedAction!=null) {
                    yodaEnvironment.exe.execute(selectedAction);
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
