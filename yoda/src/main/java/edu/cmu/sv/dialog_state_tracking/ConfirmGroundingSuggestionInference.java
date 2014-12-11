package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ConfirmValueSuggestion;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/18/14.
 */
public class ConfirmGroundingSuggestionInference extends DialogStateUpdateInference {
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
                if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Accept.class)) {
                    for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                        String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                        DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                        DiscourseUnitHypothesis updatedPredecessor = newDialogStateHypothesis.discourseUnitHypothesisMap.get(predecessorId);
                        if (!updatedPredecessor.initiator.equals("user"))
                            continue;
                        if (updatedPredecessor.getSpokenByMe()==null)
                            continue;
                        Set<String> suggestionPaths = updatedPredecessor.getSpokenByMe().findAllPathsToClass(Suggested.class.getSimpleName());
                        if (suggestionPaths.size() != 1)
                            continue;
                        String suggestionPath = new LinkedList<>(suggestionPaths).get(0);
                        JSONObject suggestedContent = (JSONObject) updatedPredecessor.getSpokenByMe().deepCopy().
                                newGetSlotPathFiller(suggestionPath+"."+HasValue.class.getSimpleName());

                        SemanticsModel newSpokenByThemHypothesis = updatedPredecessor.getSpokenByMe().deepCopy();
                        SemanticsModel.unwrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(suggestionPath),
                                HasValue.class.getSimpleName());


                        Double descriptionMatch = 0.0;
                        // verify that the suggestion agrees with the grounded hypothesis
                        if (updatedPredecessor.groundTruth.newGetSlotPathFiller("dialogAct").
                                equals(RequestConfirmValue.class.getSimpleName())){
                            JSONObject groundedIndividual = (JSONObject)updatedPredecessor.groundInterpretation.newGetSlotPathFiller(suggestionPath);
                            descriptionMatch = ReferenceResolution.descriptionMatch(yodaEnvironment, groundedIndividual, suggestedContent);
//                            System.out.println("Confirm grounding suggestion inference: description match:"+descriptionMatch);
                            if (descriptionMatch==null)
                                descriptionMatch=0.0;
                        } else {
                            continue;
                        }

                        updatedPredecessor.timeOfLastActByThem = timeStamp;
                        updatedPredecessor.spokenByThem = newSpokenByThemHypothesis;
                        updatedPredecessor.spokenByMe = null;
                        updatedPredecessor.timeOfLastActByMe = null;
                        resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
                        Double score = descriptionMatch *
                                Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(
                                updatedPredecessor, newDialogStateHypothesis, "system")) *
                                Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(
                                        updatedPredecessor, newDialogStateHypothesis, "user"));
                        resultDistribution.put(newDialogStateHypothesisID, score);
                        System.out.println("accepting a grounding suggestion: predecessor:" + predecessorId + ", score:" + score);
                    }

                } else if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Fragment.class)) {
                    // todo: interpret the fragment as a confirmation if it has an attachment point
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
