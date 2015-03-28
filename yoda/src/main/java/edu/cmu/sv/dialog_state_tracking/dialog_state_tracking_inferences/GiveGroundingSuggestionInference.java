package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.*;
import edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences.DialogStateUpdateInference;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.Suggested;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/17/14.
 */
public class GiveGroundingSuggestionInference extends DialogStateUpdateInference {
    @Override
    public Pair<Map<String, DialogState>, StringDistribution> applyAll(YodaEnvironment yodaEnvironment,
                                                                                 DialogState currentState,
                                                                                 Turn turn, long timeStamp) {
        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogState> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")){

        } else { // if turn.speaker.equals("system")

            SemanticsModel hypModel = turn.systemUtterance;
            SemanticsModel groundedHypModel = turn.groundedSystemMeaning;
            String dialogAct = hypModel.getSlotPathFiller("dialogAct");
            if (RequestConfirmValue.class.getSimpleName().equals(dialogAct)) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    try{
                        Assert.verify(!predecessor.initiator.equals("system"));
                        DiscourseAnalysis analysis = new DiscourseAnalysis(predecessor, yodaEnvironment);
                        analysis.analyseValidity();
                        Assert.verify(!analysis.ungrounded());
                    } catch (Assert.AssertException e){
                        continue;
                    }


                    JSONObject daContent = (JSONObject) hypModel.newGetSlotPathFiller("topic");
                    JSONObject groundedDaContent = (JSONObject) groundedHypModel.newGetSlotPathFiller("topic");


                    Triple<Set<String>, Set<String>, Set<String>> resolutionInformation = Utils.resolutionInformation(predecessor);
                    Set<String> slotPathsToResolve = resolutionInformation.getLeft();
                    Set<String> slotPathsToInfer = resolutionInformation.getMiddle();
                    Set<String> alreadyResolvedPaths = resolutionInformation.getRight();

                    Set<String> possiblePointsOfAttachment = new HashSet<>();
                    possiblePointsOfAttachment.addAll(slotPathsToResolve);
                    possiblePointsOfAttachment.addAll(slotPathsToInfer);
                    possiblePointsOfAttachment.addAll(alreadyResolvedPaths);
                    Set<String> attachmentPaths = Utils.filterSlotPathsByRangeClass(possiblePointsOfAttachment,
                            (String)daContent.get("class"));

                    SemanticsModel suggestion = new SemanticsModel(daContent.toJSONString());
                    SemanticsModel groundedSuggestion = new SemanticsModel(groundedDaContent.toJSONString());

                    for (String attachmentPoint : attachmentPaths) {
                        String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                        DialogState newDialogState = currentState.deepCopy();
                        DiscourseUnit updatedPredecessor = newDialogState.discourseUnitHypothesisMap.get(predecessorId);

                        // determine newSpokenByMeHypothesis: copy what they said,
                        // overwrite at the attachment point with the suggestion
                        // and wrap in "suggested"
                        SemanticsModel newSpokenByMeHypothesis = updatedPredecessor.getSpokenByThem().deepCopy();
                        newSpokenByMeHypothesis.putAtPath(newSpokenByMeHypothesis.getInternalRepresentation(), "dialogAct", dialogAct);
                        if (newSpokenByMeHypothesis.newGetSlotPathFiller(attachmentPoint)==null){
                            SemanticsModel.putAtPath(newSpokenByMeHypothesis.getInternalRepresentation(),
                                    attachmentPoint, suggestion.deepCopy().getInternalRepresentation());
                        } else {
                            newSpokenByMeHypothesis.extendAndOverwriteAtPoint(attachmentPoint, suggestion.deepCopy());
                        }
                        SemanticsModel.wrap((JSONObject) newSpokenByMeHypothesis.newGetSlotPathFiller(attachmentPoint),
                                Suggested.class.getSimpleName(), HasValue.class.getSimpleName());

                        // determine newGroundTruth by copying groundInterpretation, then
                        // overwrite at the attachment point with the suggestion
                        // DO NOT wrap in "suggested"
                        SemanticsModel newGroundTruth = updatedPredecessor.getGroundInterpretation().deepCopy();
                        if (newGroundTruth.newGetSlotPathFiller(attachmentPoint)==null){
                            SemanticsModel.putAtPath(newGroundTruth.getInternalRepresentation(),
                                    attachmentPoint, groundedSuggestion.deepCopy().getInternalRepresentation());
                        } else {
                            newGroundTruth.extendAndOverwriteAtPoint(attachmentPoint, groundedSuggestion.deepCopy());
                        }
//                        SemanticsModel.wrap((JSONObject) newGroundTruth.newGetSlotPathFiller(attachmentPoint),
//                                Suggested.class.getSimpleName(), HasValue.class.getSimpleName());

                        Utils.unground(updatedPredecessor, newSpokenByMeHypothesis, newGroundTruth, timeStamp);
                        resultHypotheses.put(newDialogStateHypothesisID, newDialogState);
                        Double score = Utils.discourseUnitContextProbability(newDialogState, updatedPredecessor);
                        resultDistribution.put(newDialogStateHypothesisID, score);
                    }
                }
            }
        }
        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }
}
