package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.utils.Assert;
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
public class ElaborateInference extends DialogStateUpdateInference {
    @Override
    public Pair<Map<String, DialogState>, StringDistribution> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {
        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogState> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")) {
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()) {
                SemanticsModel hypModel = turn.hypotheses.get(sluHypothesisID);
                String dialogAct = hypModel.getSlotPathFiller("dialogAct");
                if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Accept.class)) {
                    for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                        String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                        DialogState newDialogState = currentState.deepCopy();
                        DiscourseUnit predecessor = newDialogState.discourseUnitHypothesisMap.get(predecessorId);

                        DiscourseAnalysis duAnalysis = new DiscourseAnalysis(predecessor, yodaEnvironment);
                        try {
                            Assert.verify(!predecessor.initiator.equals("system"));
                            duAnalysis.analyseSlotFilling();
                        } catch (Assert.AssertException e){
                            continue;
                        }

                        //todo: implement the rest

                        // copy suggestion and ground the discourse unit
                        SemanticsModel newSpokenByThemHypothesis = predecessor.getSpokenByMe().deepCopy();
                        SemanticsModel.unwrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(duAnalysis.suggestionPath),
                                HasValue.class.getSimpleName());
                        Utils.returnToGround(predecessor, newSpokenByThemHypothesis, timeStamp);

                        // collect the result
                        resultHypotheses.put(newDialogStateHypothesisID, newDialogState);
                        Double score = duAnalysis.descriptionMatch *
                                Utils.discourseUnitContextProbability(newDialogState, predecessor);
                        resultDistribution.put(newDialogStateHypothesisID, score);
                    }

                } else if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Fragment.class)) {
                    // todo: interpret the fragment as a confirmation if it has an attachment point
                }
            }
        } else { // if turn.speaker.equals("system")
            //todo: implement
        }
        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }

}
