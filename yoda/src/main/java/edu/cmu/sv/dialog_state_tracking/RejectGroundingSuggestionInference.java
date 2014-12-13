package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Reject;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/18/14.
 */
public class RejectGroundingSuggestionInference extends DialogStateUpdateInference {
    @Override
    public Pair<Map<String, DialogStateHypothesis>, StringDistribution> applyAll(
            YodaEnvironment yodaEnvironment, DialogStateHypothesis currentState, Turn turn, long timeStamp) {
        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogStateHypothesis> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")) {
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()) {
                SemanticsModel hypModel = turn.hypotheses.get(sluHypothesisID);
                String dialogAct = hypModel.getSlotPathFiller("dialogAct");
                if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Reject.class)) {
                    for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                        String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                        DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                        DiscourseUnitHypothesis predecessor = newDialogStateHypothesis.discourseUnitHypothesisMap.get(predecessorId);

                        Utils.DiscourseUnitAnalysis duAnalysis = new Utils.DiscourseUnitAnalysis(predecessor, yodaEnvironment);
                        try {
                            Assert.verify(predecessor.initiator.equals("user"));
                            Assert.verify(duAnalysis.ungroundedByAct(RequestConfirmValue.class));
                            duAnalysis.analyseSuggestions();
                        } catch (Assert.AssertException e){
                            continue;
                        }

                        // copy suggestion and ground the discourse unit
                        SemanticsModel newSpokenByThemHypothesis = predecessor.getSpokenByMe().deepCopy();
                        SemanticsModel.unwrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(duAnalysis.suggestionPath),
                                HasValue.class.getSimpleName());
                        Utils.returnToGround(predecessor, newSpokenByThemHypothesis, timeStamp);

                        // collect the result
                        resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
                        Double score = (1 - duAnalysis.descriptionMatch) *
                                Utils.discourseUnitContextProbability(newDialogStateHypothesis, predecessor);
                        resultDistribution.put(newDialogStateHypothesisID, score);
                    }

                }
            }
        } else { // if turn.speaker.equals("system")
            SemanticsModel hypModel = turn.systemUtterance;
            String dialogAct = hypModel.getSlotPathFiller("dialogAct");

//            if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(ConfirmValueSuggestion.class)) {
//
//                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
//                    String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
//                    DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
//                    DiscourseUnitHypothesis updatedPredecessor = newDialogStateHypothesis.discourseUnitHypothesisMap.get(predecessorId);
//                    Set<String> suggestionPaths = updatedPredecessor.getSpokenByThem().findAllPathsToClass(Suggested.class.getSimpleName());
//
//                    SemanticsModel newSpokenByMeHypothesis = updatedPredecessor.getSpokenByThem().deepCopy();
//                    for (String acceptancePath : suggestionPaths) {
//                        SemanticsModel.unwrap((JSONObject) newSpokenByMeHypothesis.newGetSlotPathFiller(acceptancePath),
//                                HasValue.class.getSimpleName());
//                    }
//
//                    updatedPredecessor.timeOfLastActByMe = timeStamp;
//                    updatedPredecessor.spokenByMe = newSpokenByMeHypothesis;
//                    updatedPredecessor.timeOfLastActByThem = null;
//                    updatedPredecessor.spokenByThem = null;
//                    resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
//                    Double score = Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(
//                            updatedPredecessor, newDialogStateHypothesis, "system")) *
//                            Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(
//                                    updatedPredecessor, newDialogStateHypothesis, "user"));
//                    resultDistribution.put(newDialogStateHypothesisID, score);
//                }
//            }
        }
        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }

}
