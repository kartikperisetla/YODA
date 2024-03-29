package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.*;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/17/14.
 */
public class GiveGroundingSuggestionInference extends DialogStateUpdateInference {
    @Override
    public NBestDistribution<DialogState> applyAll(YodaEnvironment yodaEnvironment,
                                                                                 DialogState currentState,
                                                                                 Turn turn, long timeStamp) {
        NBestDistribution<DialogState> resultHypotheses = new NBestDistribution<>();

        if (turn.speaker.equals("user")){

        } else { // if turn.speaker.equals("system")

            SemanticsModel hypModel = turn.systemUtterance;
            SemanticsModel groundedHypModel = turn.groundedSystemMeaning;
            String dialogAct = hypModel.getSlotPathFiller("dialogAct");
            if (RequestConfirmValue.class.getSimpleName().equals(dialogAct)) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    double contextAppropriateness = Utils.discourseUnitContextProbability(currentState, predecessor);

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
//                    System.err.println("GiveGroundingSuggestionInference: possible points of attachment:"+possiblePointsOfAttachment);
//                    System.err.println("GiveGroundingSuggestionInference: filter by class:" + daContent.get("class"));
                    Set<String> attachmentPaths = Utils.filterSlotPathsByRangeClass(possiblePointsOfAttachment,
                            (String)daContent.get("class"));

                    SemanticsModel suggestion = new SemanticsModel(daContent.toJSONString());
                    SemanticsModel groundedSuggestion = new SemanticsModel(groundedDaContent.toJSONString());

                    for (String attachmentPoint : attachmentPaths) {
//                        System.err.println("GiveGroundingSuggestionInference: attachmentPoint:"+attachmentPoint);

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
                                YodaSkeletonOntologyRegistry.suggested.name, YodaSkeletonOntologyRegistry.hasValue.name);

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
//                                YodaSkeletonOntologyRegistry.suggested.name, YodaSkeletonOntologyRegistry.hasValue.name);

                        Utils.unground(updatedPredecessor, newSpokenByMeHypothesis, newGroundTruth, timeStamp);
                        resultHypotheses.put(newDialogState, contextAppropriateness);
                    }
                }
            }
        }
        return resultHypotheses;
    }
}
