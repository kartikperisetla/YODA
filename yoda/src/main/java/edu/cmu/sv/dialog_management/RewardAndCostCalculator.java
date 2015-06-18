package edu.cmu.sv.dialog_management;

import com.google.common.primitives.Doubles;
import edu.cmu.sv.dialog_state_tracking.*;
import edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences.DialogLostInference;
import edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences.MisunderstoodTurnInference;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.WebResource;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasURI;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTaskPreferences;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.utils.StringDistribution;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 *
 * Contains standard values and functions that are used to compute utility for possible system actions
 *
 * The basic rule-of-thumb is that 1 clarificationReward ~= the clarificationReward for successfully executing a dialog task.
 * To set a clarificationReward for some other condition, estimate the relative importance of that condition
 * compared to successfully completing a dialog task.
 *
 */
public class RewardAndCostCalculator {
    public static double penaltyForSpeaking = .5;
    public static double valueOfInformation = 2.0;
    public static double penaltyForIgnoringUserRequest = 2;
    public static double rewardForCorrectDialogTaskExecution = 5;
    public static double rewardForFillingRequiredSlot = 1.0;
    public static double penaltyForIncorrectDialogTaskExecution = 10;
    public static double penaltyForSpeakingOutOfTurn = 2.0;

    public static Double rewardForRequestFixMisunderstanding(DialogState dialogState, DiscourseUnit discourseUnit){
        return 1.0 * Utils.discourseUnitContextProbability(dialogState, discourseUnit) *
                ((discourseUnit.getFromInitiator("dialogAct")).equals(MisunderstoodTurnInference.duString) ? 2 : -1);
    }

    public static Double rewardForOOCRespondToRequestListOptions(DialogState dialogState, DiscourseUnit discourseUnit){
        return 1.0 * Utils.discourseUnitContextProbability(dialogState, discourseUnit) *
                ((discourseUnit.getFromInitiator("dialogAct")).equals(RequestListOptions.class.getSimpleName()) ? 2 : -1);
    }

    public static Double rewardForOOCRespondToRequestSearchAlternative(DialogState dialogState, DiscourseUnit discourseUnit){
        return 1.0 * Utils.discourseUnitContextProbability(dialogState, discourseUnit) *
                ((discourseUnit.getFromInitiator("dialogAct")).equals(RequestSearchAlternative.class.getSimpleName()) ? 2 : -1);
    }

    public static Double rewardForDialogLost(DialogState dialogState, DiscourseUnit discourseUnit){
        return 1.0 * Utils.discourseUnitContextProbability(dialogState, discourseUnit) *
                ((discourseUnit.getFromInitiator("dialogAct")).equals(DialogLostInference.duString) ? 2 : -1);
    }

    public static Double nonDialogTaskReward(NonDialogTask task,
                                             NBestDistribution<DialogState> dialogStateDistribution){

//        System.out.println("task:" + task);
        // find the probability that an equivalent task is being requested
        Double probabilityTaskAppropriate = 0.0;
        for (DialogState currentDialogState : dialogStateDistribution.keySet()){
            Double probabilityTaskAppropriateInDialogState = 0.0;
            for (String contextDiscourseUnitId : currentDialogState.getDiscourseUnitHypothesisMap().keySet()){
                DiscourseUnit contextDiscourseUnit = currentDialogState.getDiscourseUnitHypothesisMap().get(contextDiscourseUnitId);

                boolean anyMatches = contextDiscourseUnit.actionAnalysis.enumeratedNonDialogTasks.stream().
                        anyMatch(x -> x.evaluationMatch(task));
                boolean anyMissingVerbSlots = (contextDiscourseUnit.actionAnalysis.missingRequiredVerbSlots.size() > 0);
                probabilityTaskAppropriateInDialogState +=
                        (((!anyMissingVerbSlots) && anyMatches) ?
                                Utils.discourseUnitContextProbability(currentDialogState, contextDiscourseUnit) :
                                0);
            }
            probabilityTaskAppropriate += probabilityTaskAppropriateInDialogState * dialogStateDistribution.get(currentDialogState);
        }
        probabilityTaskAppropriate = Doubles.min(1.0, probabilityTaskAppropriate);
//        System.out.println("pAppropriate:" + probabilityTaskAppropriate);

        NonDialogTaskPreferences preferences = task.getPreferences();
        return preferences.rewardForCorrectExecution * probabilityTaskAppropriate -
                preferences.penaltyForIncorrectExecution * (1 - probabilityTaskAppropriate);
    }

    public static boolean answerAlreadyProvided(DiscourseUnit predecessor, DialogState dsHypothesis){
        return answerObliged(predecessor) &&
                dsHypothesis.getArgumentationLinks().stream().anyMatch(
                        x -> dsHypothesis.getDiscourseUnitHypothesisMap().get(x.getPredecessor()).equals(predecessor));
    }

    /*
    * Return weather or not the predecessor obliges a response
    * */
    public static boolean answerObliged(DiscourseUnit predecessor){
        String predecessorDialogAct = (String) predecessor.getFromInitiator("dialogAct");
        return DialogRegistry.discourseUnitDialogActs.contains(DialogRegistry.dialogActNameMap.get(predecessorDialogAct));
    }

    public static Double discourseIndependentStatementReward(DialogAct dialogAct, DiscourseUnit discourseUnit) {
        double probabilityAppropriateInContext = 0.0;
        if (discourseUnit.actionAnalysis.responseStatement.isEmpty()) {
            probabilityAppropriateInContext = 0.0;
        } else {
            String dialogActString = (String)discourseUnit.actionAnalysis.responseStatement.get("dialogAct");
            if (dialogActString.equals(DontKnow.class.getSimpleName()) && dialogAct instanceof DontKnow) {
                probabilityAppropriateInContext = 1.0;
            } else if (dialogActString.equals(DontKnow.class.getSimpleName())
                    || dialogAct instanceof DontKnow) {
                probabilityAppropriateInContext = 0.0;
            } else if (dialogActString.equals(SearchReturnedNothing.class.getSimpleName())
                    && dialogAct.getBoundClasses().get("verb_class").equals(YodaSkeletonOntologyRegistry.hasProperty.name)
                    && SemanticsModel.contentEqual(
                    new SemanticsModel((JSONObject) dialogAct.getBoundDescriptions().get("asserted_role_description")),
                    new SemanticsModel((JSONObject) discourseUnit.actionAnalysis.responseStatement.get("verb.Patient")))) {
                probabilityAppropriateInContext = 1.0;
            } else if (dialogActString.equals(Statement.class.getSimpleName())
                    && dialogAct.getBoundIndividuals().get("topic_individual").equals(
                    ((JSONObject) discourseUnit.actionAnalysis.responseStatement.get("verb.Agent")).get("HasURI"))
                    && dialogAct.getBoundClasses().get("verb_class").equals(YodaSkeletonOntologyRegistry.hasProperty.name)
                    && SemanticsModel.contentEqual(
                    new SemanticsModel((JSONObject) dialogAct.getBoundDescriptions().get("asserted_role_description")),
                    new SemanticsModel((JSONObject) discourseUnit.actionAnalysis.responseStatement.get("verb.Patient")))) {
                probabilityAppropriateInContext = 1.0;
            }
        }
//        System.out.println("dialog act:" + dialogAct.getClass().getSimpleName() + ", probability appropriate in context:" + probabilityAppropriateInContext);
        return rewardForCorrectDialogTaskExecution * probabilityAppropriateInContext -
                penaltyForIncorrectDialogTaskExecution * (1 - probabilityAppropriateInContext);

    }


    /*
    * Return the probability that there is an outstanding clarification request on a discourse unit initiated by
    * initiator
    * */
    public static Double outstandingGroundingRequest(NBestDistribution<DialogState> dialogStateDistribution,
                                                     String initiator){
        Double ans = 0.0;
        for (DialogState currentDialogState : dialogStateDistribution.keySet()){
            for (DiscourseUnit discourseUnit : currentDialogState.getDiscourseUnitHypothesisMap().values()){
                Double probabilityActive = Utils.discourseUnitContextProbability(currentDialogState, discourseUnit);
                if (!discourseUnit.getInitiator().equals(initiator))
                    continue;
                if (initiator.equals("system")) {
                    if (discourseUnit.getSpokenByThem()!=null)
                        ans += probabilityActive * dialogStateDistribution.get(currentDialogState);
                } else { // initiator = "user"
                    if (discourseUnit.getSpokenByMe()!=null)
                        ans += probabilityActive * dialogStateDistribution.get(currentDialogState);
                }
            }
        }
//        System.out.println("outstanding grounding request:" + ans);
        return Doubles.min(ans, 1.0);
    }


    public static Double heuristicClarificationReward(NBestDistribution<DialogState> dialogStateDistribution,
                                                      String valueURI){
        NBestDistribution<DialogState> futureStateDistributionIfConfirmed = new NBestDistribution<>();
        NBestDistribution<DialogState> futureStateDistributionIfRejected = new NBestDistribution<>();

        Double probabilityOfValue = 0.0;
        Double probabilityClarificationRequestAppropriate = 0.0;

        for (DialogState dialogState : dialogStateDistribution.keySet()){
            futureStateDistributionIfConfirmed.put(dialogState, 0.0);
            futureStateDistributionIfRejected.put(dialogState, 0.0);

            for (DiscourseUnit contextDiscourseUnit : dialogState.getDiscourseUnitHypothesisMap().values()){
                if (contextDiscourseUnit.getInitiator().equals("system"))
                    continue;
                Double discourseUnitConfidence = Utils.discourseUnitContextProbability(dialogState, contextDiscourseUnit);
                boolean anyMatches = false;
//                System.err.println("contextDU:");
//                System.err.println(contextDiscourseUnit);
                for (String path : contextDiscourseUnit.getGroundInterpretation().findAllPathsToClass(YodaSkeletonOntologyRegistry.webResource.name)){
                    if (contextDiscourseUnit.getGroundInterpretation().newGetSlotPathFiller(path+"."+ YodaSkeletonOntologyRegistry.hasUri.name).equals(valueURI)){
                        anyMatches = true;
                        break;
                    }
                }
                if (anyMatches) {
                    futureStateDistributionIfConfirmed.put(dialogState,
                            futureStateDistributionIfConfirmed.get(dialogState) +
                            discourseUnitConfidence * dialogStateDistribution.get(dialogState));
                    probabilityOfValue += discourseUnitConfidence * dialogStateDistribution.get(dialogState);
                } else {
                    futureStateDistributionIfRejected.put(dialogState,
                            futureStateDistributionIfRejected.get(dialogState) +
                                    discourseUnitConfidence * dialogStateDistribution.get(dialogState));
                }

                probabilityClarificationRequestAppropriate += answerObliged(contextDiscourseUnit) &&
                        !answerAlreadyProvided(contextDiscourseUnit, dialogState) ? discourseUnitConfidence : 0;
            }
        }

        probabilityOfValue = 1 - probabilityOfValue <= .00001 ? .99999 : probabilityOfValue;
        probabilityOfValue = probabilityOfValue <= .00001 ? .00001 : probabilityOfValue;
        futureStateDistributionIfConfirmed.put(new DialogState(), MisunderstoodTurnInference.probabilityUserTurnMisunderstood);
        futureStateDistributionIfRejected.put(new DialogState(), MisunderstoodTurnInference.probabilityUserTurnMisunderstood);
        futureStateDistributionIfConfirmed.normalize();
        futureStateDistributionIfRejected.normalize();

        double reward =  valueOfInformation * (dialogStateDistribution.information() -
                probabilityOfValue * futureStateDistributionIfConfirmed.information() -
                (1 - probabilityOfValue) * futureStateDistributionIfRejected.information());
        reward = probabilityClarificationRequestAppropriate * reward;
//        System.out.println("current information" + dialogStateDistribution.information() + ", probability of value" + probabilityOfValue + ", reward:" + reward);
        return reward;

//        Double informationOfDialogState = dialogStateDistribution.information();
//        Double informationOfValue = -1.0 * Math.log(probabilityOfValue) * probabilityOfValue +
//                -1.0 * Math.log(1 - probabilityOfValue) * (1 - probabilityOfValue);
//        System.out.println("reward:" + informationOfDialogState * probabilityOfValue * Math.log(1 + informationOfValue) * valueOfInformation);
//        return informationOfDialogState * probabilityOfValue * Math.log(1 + informationOfValue) * valueOfInformation;
    }

    /*
    * Confirming a value is confirming that some role is filled by it,
    * it does not confirm anything about which role it fills
    * */
    public static StringDistribution predictConfidenceGainFromValueConfirmation(StringDistribution dialogStateDistribution,
                                                                                Map<String, DialogState> dialogStateHypotheses,
                                                                                String valueUri){
        if (dialogStateDistribution.keySet().size()!=dialogStateHypotheses.size()){
            System.out.println("BIG PROBLEM:\n"+dialogStateHypotheses+"\n"+dialogStateDistribution);
            System.exit(0);
        }
        double limit = .8; // we will never predict 100% confidence gain
        StringDistribution ans = new StringDistribution();
        // find the degree to which each dialog state hypothesis has the valueURI
        // (depends on which discourse units are most recent)
        Map<String, Double> hasValueMap = new HashMap<>();
        for (String dialogStateHypothesisId : dialogStateDistribution.keySet()){
            hasValueMap.put(dialogStateHypothesisId, 0.0);
            DialogState dialogState = dialogStateHypotheses.get(dialogStateHypothesisId);
            for (DiscourseUnit contextDiscourseUnit : dialogState.getDiscourseUnitHypothesisMap().values()){
//                System.out.println("predicting confidence gain: contextDU:\n"+contextDiscourseUnit);
                if (contextDiscourseUnit.getInitiator().equals("system"))
                    continue;
                Double discourseUnitConfidence = Utils.discourseUnitContextProbability(dialogState, contextDiscourseUnit);
//                discourseUnitConfidence *= Math.pow(.1, Utils.numberOfLinksRespondingToDiscourseUnit(contextDiscourseUnit, dialogStateHypothesis));
                boolean anyMatches = false;
                for (String path : contextDiscourseUnit.getGroundInterpretation().findAllPathsToClass(YodaSkeletonOntologyRegistry.webResource.name)){
//                    System.out.println("path:"+path);
                    if (contextDiscourseUnit.getGroundInterpretation().newGetSlotPathFiller(path+"."+ YodaSkeletonOntologyRegistry.hasUri.name).equals(valueUri)){
                        anyMatches = true;
                        break;
                    }
                }
                if (anyMatches) {
                    hasValueMap.put(dialogStateHypothesisId, Doubles.min(1.0, hasValueMap.get(dialogStateHypothesisId) + discourseUnitConfidence));
                }
            }
        }

        // compute the predicted confidence gain based on hasValueMap
        Double totalMassEliminated = dialogStateHypotheses.keySet().stream().
                map(x -> dialogStateDistribution.get(x) * (1 - hasValueMap.get(x))).
                reduce(0.0, (x, y) -> x + y);
        // distribute the eliminated probability mass among the non-eliminated hypotheses
        for (String key : dialogStateHypotheses.keySet()) {
            ans.put(key, limit * (1 - dialogStateDistribution.get(key)) * hasValueMap.get(key) * totalMassEliminated);
        }
//        System.out.println("RewardAndCostCalculator.predictConfidenceGainFromValueConfirmation: ans:\n"+ans);
        return ans;
    }


    public static Double requestSlotFillingReward(DialogState dialogState,
                                                  DiscourseUnit discourseUnit,
                                                  DialogAct dialogAct){

        if (discourseUnit.actionAnalysis.missingRequiredVerbSlots.contains(
                (String) dialogAct.getBoundPaths().get("requested_role_path"))) {
            return rewardForFillingRequiredSlot;
        }
        return 0.0;
    }

}
