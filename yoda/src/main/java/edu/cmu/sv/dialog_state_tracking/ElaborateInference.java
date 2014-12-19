package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.dialog_task.ReferenceResolution;
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
 *
 * Elaborating is adding in additional details to an existing discourse unit
 *
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
                if (DialogRegistry.discourseUnitDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))) {
                    // todo: implement non-fragment case
                } else if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Fragment.class)) {
                    for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                        DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId).deepCopy();

                        JSONObject topicContent;
                        DiscourseAnalysis duAnalysis = new DiscourseAnalysis(predecessor, yodaEnvironment);
                        try {
                            Assert.verify(!predecessor.initiator.equals("system"));
                            Assert.verify(hypModel.newGetSlotPathFiller("topic")!=null);
                            topicContent = (JSONObject)hypModel.newGetSlotPathFiller("topic");
                            duAnalysis.analyseSlotFilling();
                        } catch (Assert.AssertException e){
                            continue;
                        }

                        // copy suggestion and ground the discourse unit
                        SemanticsModel newSpokenByThemHypothesis = predecessor.getSpokenByMe().deepCopy();
                        SemanticsModel.putAtPath(newSpokenByThemHypothesis.getInternalRepresentation(),
                                duAnalysis.requestPath,
                                topicContent);
                        Utils.returnToGround(predecessor, newSpokenByThemHypothesis, timeStamp);

                        Pair<Map<String, DiscourseUnit>, StringDistribution> groundedHypotheses =
                                ReferenceResolution.resolve(predecessor, yodaEnvironment);
                        for (String groundedDuKey: groundedHypotheses.getRight().keySet()) {
                            String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                            DialogState newDialogState = currentState.deepCopy();
                            DiscourseUnit currentDu = groundedHypotheses.getLeft().get(groundedDuKey);
                            newDialogState.getDiscourseUnitHypothesisMap().put(predecessorId, currentDu);

                            currentDu.actionAnalysis.update(yodaEnvironment, currentDu);
                            Double score = Utils.discourseUnitContextProbability(newDialogState, currentDu) *
                                    groundedHypotheses.getRight().get(groundedDuKey);
                            resultDistribution.put(newDialogStateHypothesisID, score);
                            resultHypotheses.put(newDialogStateHypothesisID, newDialogState);
                        }
                    }
                }
            }
        } else { // if turn.speaker.equals("system")
            //todo: implement
        }
        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }

}
