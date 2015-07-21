package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.*;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRole;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRoleGivenRole;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.Set;

/**
 * Created by David Cohen on 10/17/14.
 */
public class RequestSlotInference extends DialogStateUpdateInference {
    @Override
    public NBestDistribution<DialogState> applyAll(YodaEnvironment yodaEnvironment,
                                                                                 DialogState currentState,
                                                                                 Turn turn, long timeStamp) {
        NBestDistribution<DialogState> resultHypotheses = new NBestDistribution<>();

        if (turn.speaker.equals("system")){
            SemanticsModel hypModel = turn.systemUtterance;
            String dialogAct = hypModel.getSlotPathFiller("dialogAct");
            if (RequestRoleGivenRole.class.getSimpleName().equals(dialogAct) ||
                    RequestRole.class.getSimpleName().equals(dialogAct)) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    String givenPath = null;
                    JSONObject givenContent;
                    String requestPath;
                    DiscourseAnalysis analysis;
                    try {
                        Assert.verify(!predecessor.initiator.equals("system"));
                        analysis = new DiscourseAnalysis(predecessor, yodaEnvironment);
                        analysis.analyseValidity();
//                        analysis.analyseSlotFilling();
                        JSONObject verbObject = (JSONObject) hypModel.newGetSlotPathFiller("verb");
                        Set<String> requestPaths = hypModel.findAllPathsToClass(YodaSkeletonOntologyRegistry.requested.name);
                        Assert.verify(requestPaths.size() == 1);

                        requestPath = new LinkedList<>(requestPaths).get(0);
                        Assert.verify(predecessor.getFromInitiator(requestPath) == null);
                        Assert.verify(
                                (verbObject.keySet().size() == 3 && dialogAct.equals(RequestRoleGivenRole.class.getSimpleName())) ||
                                        (verbObject.keySet().size() == 2 && dialogAct.equals(RequestRole.class.getSimpleName())));
                        for (Object key : verbObject.keySet()) {
                            if (key.equals("class") || ("verb." + key).equals(requestPath))
                                continue;
                            givenPath = "verb." + key;
                        }
                        if (givenPath != null)
                            givenContent = (JSONObject) hypModel.newGetSlotPathFiller(givenPath);
                    } catch (Assert.AssertException e) {
                        continue;
                    }


                    // calculate degree of match between givenContent and whatever the previously resolved content was
                    // (or if the role isn't supposed to be resolved, see if the descriptions are compatible)
                    //todo: calculate for real
                    Double givenMatch = 1.0;

                    // determine the value for spokenByMe:
                    // copy spokenByThem
                    // insert Requested object at appropriate path

                    DialogState newDialogState = currentState.deepCopy();
                    DiscourseUnit updatedPredecessor = newDialogState.discourseUnitHypothesisMap.get(predecessorId);

                    SemanticsModel newSpokenByMeHypothesis = updatedPredecessor.getSpokenByThem().deepCopy();
                    SemanticsModel.putAtPath(newSpokenByMeHypothesis.getInternalRepresentation(),
                            requestPath,
                            SemanticsModel.parseJSON("{\"class\":\""+ YodaSkeletonOntologyRegistry.requested.name+"\"}"));

                    Utils.unground(updatedPredecessor, newSpokenByMeHypothesis, turn.groundedSystemMeaning, timeStamp);
                    Double score = givenMatch *
                            Utils.discourseUnitContextProbability(newDialogState, updatedPredecessor);
                    resultHypotheses.put(newDialogState, score);

                }
            }
        }
        return resultHypotheses;
    }
}
