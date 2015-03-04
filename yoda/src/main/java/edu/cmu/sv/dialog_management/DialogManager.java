package edu.cmu.sv.dialog_management;

import edu.cmu.sv.database.dialog_task.ActionEnumeration;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Statement;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ClarificationDialogAct;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Contains a dialog state tracker and specification of interfaces, etc.
 * Contains functions for assessing potential dialog moves.
 * Contains a main method which is the dialog agent loop.
 *
 */
public class DialogManager implements Runnable {
    private boolean outstandingSystemAction = false; // the dialog manager locks up while there is an outstanding system action un-detected by the DST
    private static Logger logger = Logger.getLogger("yoda.dialog_management.DialogManager");
    static {
        try {
            if (YodaEnvironment.mongoLoggingActive){
                MongoLogHandler handler = new MongoLogHandler();
                logger.addHandler(handler);
            } else {
                FileHandler fh;
                fh = new FileHandler("DialogManager.log");
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    YodaEnvironment yodaEnvironment;
    StringDistribution dialogStateDistribution = new StringDistribution();
    Map<String, DialogState> dialogStateHypotheses = new HashMap<>();

    public void detectSystemAction(){
        outstandingSystemAction = false;
    }

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
                            getPossibleIndividualBindings(dialogActInstance, yodaEnvironment);
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
//                            if (newDialogActInstance instanceof DontKnow)
//                                System.out.println("enumerated act:"+newDialogActInstance+"\nreward:"+currentReward);
                            accumulateReward(actionExpectedReward, newDialogActInstance, currentReward);
                        }
                    }
                }


                // actions that are enumerated from action analysis:
                for (String discourseUnitHypothesisId : currentDialogState.getDiscourseUnitHypothesisMap().
                        keySet()) {
                    DiscourseUnit contextDiscourseUnit = currentDialogState.
                            getDiscourseUnitHypothesisMap().get(discourseUnitHypothesisId);
                    if (contextDiscourseUnit.actionAnalysis.responseStatement.isEmpty())
                        continue;
                    if (contextDiscourseUnit.actionAnalysis.responseStatement.get("dialogAct").equals(Statement.class.getSimpleName())){
                        Statement s = new Statement();
                        Map<String, Object> bindings = new HashMap<>();
                        bindings.put("verb_class", HasProperty.class.getSimpleName());
                        bindings.put("topic_individual",
                                ((JSONObject) contextDiscourseUnit.actionAnalysis.responseStatement.get("verb.Agent")).get("HasURI"));
                        bindings.put("asserted_role_description",
                                ((JSONObject) contextDiscourseUnit.actionAnalysis.responseStatement.get("verb.Patient")));
                        s.bindVariables(bindings);
//                        System.out.println("enumerated a statement:"+s);
                        Double currentReward = s.reward(currentDialogState, contextDiscourseUnit) *
                                dialogStateDistribution.get(dialogStateHypothesisId);
//                        System.out.println("reward: "+currentReward);
                        accumulateReward(actionExpectedReward, s, currentReward);
                    } else { // contextDiscourseUnit.actionAnalysis.responseStatement.get("dialogAct").equals(DontKnow.class.getSimpleName())){
                        // todo: make sure DontKnow hasn't already been enumerated before re-enumerating
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
                            Double currentReward = newDialogActInstance.reward(currentDialogState, contextDiscourseUnit) *
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
                        getPossibleIndividualBindings(dialogActInstance, yodaEnvironment);
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
                if (!outstandingSystemAction) {
                    Pair<Map<String, DialogState>, StringDistribution> DmInput = null;
                    // empty out the queue to get the most recent dialog state
                    while (true) {
                        Pair<Map<String, DialogState>, StringDistribution> tmp;
                        tmp = yodaEnvironment.DmInputQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (tmp == null)
                            break;
                        else
                            DmInput = tmp;
                    }
                    if (DmInput != null) {
                        dialogStateHypotheses = DmInput.getLeft();
                        dialogStateDistribution = DmInput.getRight();
                    }
                    List<Pair<SystemAction, Double>> rankedActions = enumerateAndScorePossibleActions();

                    // generate log record
                    JSONObject record = MongoLogHandler.createEventRecord("evaluated_actions");
                    JSONArray evaluatedActions = new JSONArray();
                    for (int i = 0; i < rankedActions.size(); i++) {
                        Pair<SystemAction, Double> action = rankedActions.get(i);
                        JSONObject JSONAction = SemanticsModel.parseJSON("{}");
                        JSONAction.put("score", action.getRight());
                        if (action.getLeft() == null) {
                            JSONAction.put("class", "null");
                            evaluatedActions.add(JSONAction);
                            continue;
                        }
                        JSONAction.put("class", action.getLeft().getClass().getSimpleName());
                        if (NonDialogTask.class.isAssignableFrom(action.getLeft().getClass())) {
                            JSONAction.put("task_type", NonDialogTask.class.getSimpleName());
                            JSONAction.put("task_spec", ((NonDialogTask) action.getLeft()).getTaskSpec());
                        } else if (DialogAct.class.isAssignableFrom(action.getLeft().getClass())) {
                            JSONAction.put("task_type", DialogAct.class.getSimpleName());
                            JSONAction.put("bound_individuals", new JSONObject(((DialogAct) action.getLeft()).getBoundIndividuals()));
                            JSONAction.put("bound_classes", new JSONObject(((DialogAct) action.getLeft()).getBoundClasses()));
                            JSONAction.put("bound_paths", new JSONObject(((DialogAct) action.getLeft()).getBoundPaths()));
                            JSONAction.put("bound_descriptions", new JSONObject(((DialogAct) action.getLeft()).getBoundDescriptions()));
                        }
                        evaluatedActions.add(JSONAction);
                    }
                    record.put("actions", evaluatedActions);
                    logger.info(record.toJSONString());

                    SystemAction selectedAction = rankedActions.get(0).getKey();
                    if (selectedAction != null) {
                        yodaEnvironment.exe.execute(selectedAction);
                        outstandingSystemAction = true;
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
