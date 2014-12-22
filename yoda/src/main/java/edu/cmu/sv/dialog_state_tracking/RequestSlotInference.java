package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRole;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRoleGivenRole;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by David Cohen on 10/17/14.
 */
public class RequestSlotInference extends DialogStateUpdateInference {
    @Override
    public Pair<Map<String, DialogState>, StringDistribution> applyAll(YodaEnvironment yodaEnvironment,
                                                                                 DialogState currentState,
                                                                                 Turn turn, long timeStamp) {
        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogState> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")){
            // todo: implement to understand slot requests from the user
        } else { // if turn.speaker.equals("system")

            SemanticsModel hypModel = turn.systemUtterance;
            String dialogAct = hypModel.getSlotPathFiller("dialogAct");
            if (RequestRoleGivenRole.class.getSimpleName().equals(dialogAct) ||
                    RequestRole.class.getSimpleName().equals(dialogAct)) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    String requestPath;
                    String givenPath = null;
                    JSONObject givenContent;
                    try {
                        Assert.verify(!predecessor.initiator.equals("system"));
                        JSONObject verbObject = (JSONObject) hypModel.newGetSlotPathFiller("verb");
                        Set<String> requestPaths = hypModel.findAllPathsToClass(Requested.class.getSimpleName());
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

                    String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                    DialogState newDialogState = currentState.deepCopy();
                    DiscourseUnit updatedPredecessor = newDialogState.discourseUnitHypothesisMap.get(predecessorId);

                    SemanticsModel newSpokenByMeHypothesis = updatedPredecessor.getSpokenByThem().deepCopy();
                    SemanticsModel.putAtPath(newSpokenByMeHypothesis.getInternalRepresentation(),
                            requestPath,
                            SemanticsModel.parseJSON("{\"class\":\""+Requested.class.getSimpleName()+"\"}"));

                    Utils.unground(updatedPredecessor, newSpokenByMeHypothesis, turn.groundedSystemMeaning, timeStamp);
                    resultHypotheses.put(newDialogStateHypothesisID, newDialogState);
                    Double score = givenMatch *
                            Utils.discourseUnitContextProbability(newDialogState, updatedPredecessor);
                    resultDistribution.put(newDialogStateHypothesisID, score);

                }
            }
        }
        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }
}
