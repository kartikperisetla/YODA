package edu.cmu.sv.dialog_management;

import com.google.common.primitives.Doubles;
import edu.cmu.sv.dialog_state_tracking.*;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.DontKnow;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Reject;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTaskPreferences;
import edu.cmu.sv.utils.StringDistribution;

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
    public static double penaltyForIgnoringUserRequest = 2;
    public static double rewardForCorrectDialogTaskExecution = 5;
    public static double rewardForFillingRequiredSlot = 1.0;
    public static double penaltyForIncorrectDialogTaskExecution = 10;
    public static double penaltyForSpeakingOutOfTurn = 1.0;


    public static Double rewardForRequestFixMisunderstanding(DialogState dialogState, DiscourseUnit discourseUnit){
        return 1.0 * Utils.discourseUnitContextProbability(dialogState, discourseUnit) *
                ((discourseUnit.getFromInitiator("dialogAct")).equals(MisunderstoodTurnInference.duString) ? 1 : -1);
    }

    public static Double rewardForDialogLost(DialogState dialogState, DiscourseUnit discourseUnit){
        return 1.0 * Utils.discourseUnitContextProbability(dialogState, discourseUnit) *
                ((discourseUnit.getFromInitiator("dialogAct")).equals(DialogLostInference.duString) ? 1 : -1);
    }

    public static Double nonDialogTaskReward(NonDialogTask task,
                                             Map<String, DialogState> dialogStateHypotheses,
                                             StringDistribution dialogStateDistribution){

        // find the probability that an equivalent task is being requested
        Double probabilityTaskAppropriate = 0.0;
        for (String dialogStateHypothesisId : dialogStateHypotheses.keySet()){
            DialogState currentDialogState = dialogStateHypotheses.get(dialogStateHypothesisId);
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
            probabilityTaskAppropriate += probabilityTaskAppropriateInDialogState * dialogStateDistribution.get(dialogStateHypothesisId);
        }
        probabilityTaskAppropriate = Doubles.min(1.0, probabilityTaskAppropriate);

        NonDialogTaskPreferences preferences = task.getPreferences();
        return preferences.rewardForCorrectExecution * probabilityTaskAppropriate -
                preferences.penaltyForIncorrectExecution * (1 - probabilityTaskAppropriate);
    }

    /*
    * Return the probability that this dialog act will be interpreted in this context.
    *
    * If this is a grounding act, duHypothesis is the DU that the system intends to clarify,
    * Otherwise, this is the DU that the system intends to respond to.
    * */
    public static Double probabilityInterpretedCorrectly(DiscourseUnit duHypothesis, DialogState dsHypothesis,
                                                         DialogAct dialogAct){
        if (dialogAct instanceof Accept || dialogAct instanceof Reject || dialogAct instanceof DontKnow) {
            if (!duHypothesis.getInitiator().equals("user"))
                return 0.0;
            double probabilityInterpretedThisWay = Utils.discourseUnitContextProbability(dsHypothesis, duHypothesis);
            if (answerObliged(duHypothesis) && !answerAlreadyProvided(duHypothesis, dsHypothesis))
                return probabilityInterpretedThisWay;
            else if (answerObliged(duHypothesis))
                return .1 * probabilityInterpretedThisWay;
        }
        return 0.0;
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
        String predecessorDialogAct;
        if (predecessor.getInitiator().equals("user"))
            predecessorDialogAct = (String) predecessor.getSpokenByThem().newGetSlotPathFiller("dialogAct");
        else
            predecessorDialogAct = (String) predecessor.getSpokenByMe().newGetSlotPathFiller("dialogAct");
        return predecessorDialogAct.equals(YNQuestion.class.getSimpleName());
    }

    /*
    * Return the specific clarificationReward that this argumentation act should give,
    * based on the analysis of the predecessor discourse unit
    * */
    public static Double discourseIndependentArgumentationReward(DiscourseUnit predecessorDiscourseUnit,
                                                                 DialogAct dialogAct){
//        System.out.println("discourseIndependentArgumentationReward: "+dialogAct);
        Double probabilityCorrectAnswer = 0.0;
        if (dialogAct instanceof Accept) {
            if (predecessorDiscourseUnit.actionAnalysis.ynqTruth!=null)
                probabilityCorrectAnswer = predecessorDiscourseUnit.actionAnalysis.ynqTruth;
        } else if (dialogAct instanceof Reject) {
            if (predecessorDiscourseUnit.actionAnalysis.ynqTruth!=null)
                probabilityCorrectAnswer = 1 - predecessorDiscourseUnit.actionAnalysis.ynqTruth;
        } else if (dialogAct instanceof DontKnow) {
            if (predecessorDiscourseUnit.actionAnalysis.ynqTruth==null &&
                    predecessorDiscourseUnit.actionAnalysis.missingRequiredVerbSlots.size()==0)
                probabilityCorrectAnswer = 1.0;
        }
        return rewardForCorrectDialogTaskExecution *probabilityCorrectAnswer - penaltyForIncorrectDialogTaskExecution *(1-probabilityCorrectAnswer);
    }



    /*
    * Return the probability that there is an outstanding clarification request on a discourse unit initiated by
    * initiator
    * */
    public static Double outstandingGroundingRequest(StringDistribution dialogStateDistribution,
                                                     Map<String, DialogState> dialogStateHypotheses,
                                                     String initiator){
        Double ans = 0.0;
        for (String dialogStateHypothesisId : dialogStateDistribution.keySet()){
            for (DiscourseUnit discourseUnit : dialogStateHypotheses.get(dialogStateHypothesisId).
                    getDiscourseUnitHypothesisMap().values()){
                Double probabilityActive = Utils.discourseUnitContextProbability(
                        dialogStateHypotheses.get(dialogStateHypothesisId),
                        discourseUnit);
                if (!discourseUnit.getInitiator().equals(initiator))
                    continue;
                if (initiator.equals("system")) {
                    if (discourseUnit.getSpokenByThem()!=null)
                        ans += probabilityActive * dialogStateDistribution.get(dialogStateHypothesisId);
                } else { // initiator = "user"
                    if (discourseUnit.getSpokenByMe()!=null)
                        ans += probabilityActive * dialogStateDistribution.get(dialogStateHypothesisId);
                }
            }
        }
        return Doubles.min(ans, 1.0);
    }


    /*
    * To compute the clarificationReward for a clarification dialog act,
    * estimate the improvement in clarificationReward to all possible dialog and non-dialog tasks
    * that relate to all the available DU hypotheses.
    * */
    public static Double clarificationDialogActReward(StringDistribution dialogStateDistribution,
                                                      Map<String, DialogState> dialogStateHypotheses,
                                                      StringDistribution predictedRelativeConfidenceGain) {
        Double totalReward = 0.0;
        // sum up the predicted rewards supposing that each current hypothesis is true,
        // weighting the predicted clarificationReward by the current belief that the hypothesis is true.
        for (String dialogStateHypothesisID : dialogStateHypotheses.keySet()){
            DialogState dialogState = dialogStateHypotheses.get(dialogStateHypothesisID);
            Double currentConfidence = dialogStateDistribution.get(dialogStateHypothesisID);
            Double predictedConfidence = currentConfidence + (1-currentConfidence)*
                    predictedRelativeConfidenceGain.get(dialogStateHypothesisID);

            // predict the difference in expected clarificationReward after clarification
            for (DiscourseUnit contextDiscourseUnit : dialogState.getDiscourseUnitHypothesisMap().values()) {
                if (contextDiscourseUnit.getInitiator().equals("system"))
                    continue;
                Double discourseUnitConfidence = Utils.discourseUnitContextProbability(dialogState, contextDiscourseUnit);

//                SemanticsModel spokenByThem = contextDiscourseUnit.getSpokenByThem();
//                Class<? extends DialogAct> daClass = DialogRegistry.dialogActNameMap.
//                        get((String) spokenByThem.newGetSlotPathFiller("dialogAct"));

//                // add contribution from non-dialog tasks
//                if (DialogRegistry.nonDialogTaskRegistry.containsKey(daClass)) {
//                    for (Class<? extends NonDialogTask> taskClass : DialogRegistry.nonDialogTaskRegistry.get(daClass)) {
//                        NonDialogTaskPreferences preferences = taskClass.getConstructor(Database.class).newInstance(yodaEnvironment).getPreferences();
//                        Double predictedRewardDifference = 0.0;
//                        predictedRewardDifference += predictedConfidence * preferences.rewardForCorrectExecution;
//                        predictedRewardDifference -= (1 - predictedConfidence) * preferences.penaltyForIncorrectExecution;
//                        predictedRewardDifference -= currentConfidence * preferences.rewardForCorrectExecution;
//                        predictedRewardDifference += (1 - currentConfidence) * preferences.penaltyForIncorrectExecution;
//                        totalReward += currentConfidence * discourseUnitConfidence * predictedRewardDifference /
//                                DialogRegistry.nonDialogTaskRegistry.get(daClass).size();
//                    }
//                }

                // add contribution for dialog tasks
                Double predictedRewardDifference = 0.0;
                predictedRewardDifference += predictedConfidence * rewardForCorrectDialogTaskExecution;
                predictedRewardDifference -= (1 - predictedConfidence) * penaltyForIncorrectDialogTaskExecution;
                predictedRewardDifference -= currentConfidence * rewardForCorrectDialogTaskExecution;
                predictedRewardDifference += (1 - currentConfidence) * penaltyForIncorrectDialogTaskExecution;
                totalReward += currentConfidence * discourseUnitConfidence * predictedRewardDifference;
            }
        }
        return totalReward;
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
                for (String path : contextDiscourseUnit.getGroundInterpretation().findAllPathsToClass(WebResource.class.getSimpleName())){
//                    System.out.println("path:"+path);
                    if (contextDiscourseUnit.getGroundInterpretation().newGetSlotPathFiller(path+"."+ HasURI.class.getSimpleName()).equals(valueUri)){
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
