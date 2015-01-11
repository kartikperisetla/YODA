package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()) {
                SemanticsModel hypModel = turn.hypotheses.get(sluHypothesisID);
                String dialogAct = hypModel.getSlotPathFiller("dialogAct");
                if (Fragment.class.getSimpleName().equals(dialogAct)) {
//                    for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
//                        DiscourseUnitHypothesis predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
//                        if (predecessor.initiator.equals("user"))
//                            continue;
//                        Set<String> suggestionPaths = predecessor.getSpokenByMe().findAllPathsToClass(Suggested.class.getSimpleName());
//                        if (suggestionPaths.size() > 0)
//                            continue;
//
//                        JSONObject daContent = (JSONObject) hypModel.newGetSlotPathFiller("topic");
//                        StringDistribution attachmentPoints = Utils.findPossiblePointsOfAttachment(
//                                predecessor.getSpokenByMe(), daContent);
//                        SemanticsModel wrapped = new SemanticsModel(daContent.toJSONString());
//                        SemanticsModel.wrap((JSONObject) wrapped.newGetSlotPathFiller(""),
//                                Suggested.class.getSimpleName(), HasValue.class.getSimpleName());
//
//                        for (String attachmentPoint : attachmentPoints.keySet()) {
//                            String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
//                            DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
//                            DiscourseUnitHypothesis updatedPredecessor = newDialogStateHypothesis.discourseUnitHypothesisMap.get(predecessorId);
//
//                            SemanticsModel newSpokenByThemHypothesis = updatedPredecessor.getSpokenByMe().deepCopy();
//                            newSpokenByThemHypothesis.extendAndOverwriteAtPoint(attachmentPoint, wrapped.deepCopy());
//                            SemanticsModel.wrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(attachmentPoint),
//                                    Suggested.class.getSimpleName(), HasValue.class.getSimpleName());
//
//                            updatedPredecessor.timeOfLastActByThem = timeStamp;
//                            updatedPredecessor.spokenByThem = newSpokenByThemHypothesis;
//                            resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
//                            Double score = attachmentPoints.get(attachmentPoint) *
//                                    Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(
//                                            updatedPredecessor, newDialogStateHypothesis, "system")) *
//                                    Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(
//                                            updatedPredecessor, newDialogStateHypothesis, "user"));
//                            resultDistribution.put(newDialogStateHypothesisID, score);
//                        }
//                    }
                }
            }
        } else { // if turn.speaker.equals("system")

            SemanticsModel hypModel = turn.systemUtterance;
            String dialogAct = hypModel.getSlotPathFiller("dialogAct");
            if (RequestConfirmValue.class.getSimpleName().equals(dialogAct)) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    try{
                        Assert.verify(!predecessor.initiator.equals("system"));
                        DiscourseAnalysis analysis = new DiscourseAnalysis(predecessor, yodaEnvironment);
                        analysis.analyseValidity();
//                        Set<String> suggestionPaths = predecessor.getSpokenByThem().findAllPathsToClass(Suggested.class.getSimpleName());
//                        Assert.verify(suggestionPaths.size()==0);
                    } catch (Assert.AssertException e){
                        continue;
                    }

                    JSONObject daContent = (JSONObject) hypModel.newGetSlotPathFiller("topic");
                    StringDistribution attachmentPoints = Utils.findPossiblePointsOfAttachment(
                            predecessor.getSpokenByThem(), daContent);
                    SemanticsModel suggestion = new SemanticsModel(daContent.toJSONString());

                    for (String attachmentPoint : attachmentPoints.keySet()) {
                        String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                        DialogState newDialogState = currentState.deepCopy();
                        DiscourseUnit updatedPredecessor = newDialogState.discourseUnitHypothesisMap.get(predecessorId);

                        SemanticsModel newSpokenByMeHypothesis = updatedPredecessor.getSpokenByThem().deepCopy();
                        newSpokenByMeHypothesis.extendAndOverwriteAtPoint(attachmentPoint, suggestion.deepCopy());
                        SemanticsModel.wrap((JSONObject) newSpokenByMeHypothesis.newGetSlotPathFiller(attachmentPoint),
                                Suggested.class.getSimpleName(), HasValue.class.getSimpleName());

                        Utils.unground(updatedPredecessor, newSpokenByMeHypothesis, turn.groundedSystemMeaning, timeStamp);
                        resultHypotheses.put(newDialogStateHypothesisID, newDialogState);
                        Double score = attachmentPoints.get(attachmentPoint) *
                                Utils.discourseUnitContextProbability(newDialogState, updatedPredecessor);
                        resultDistribution.put(newDialogStateHypothesisID, score);
                    }
                }
            }
        }
        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }
}
