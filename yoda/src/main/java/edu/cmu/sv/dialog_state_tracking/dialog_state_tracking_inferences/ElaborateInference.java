package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.database.ReferenceResolution;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.dialog_state_tracking.*;
import edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences.DialogStateUpdateInference;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/18/14.
 *
 * Elaborating is adding in additional details to an existing discourse unit by the original presenter
 *
 */
public class ElaborateInference extends DialogStateUpdateInference {
    @Override
    public NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {
        NBestDistribution<DialogState> resultHypotheses = new NBestDistribution<>();

        if (turn.speaker.equals("user")) {
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()) {
                SemanticsModel hypModel = turn.hypotheses.get(sluHypothesisID);
                Double sluScore = turn.hypothesisDistribution.get(sluHypothesisID);
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
                                ReferenceResolution.resolveDiscourseUnit(predecessor, yodaEnvironment);

                        for (String groundedDuKey: groundedHypotheses.getRight().keySet()) {
                            DialogState newDialogState = currentState.deepCopy();
                            DiscourseUnit currentDu = groundedHypotheses.getLeft().get(groundedDuKey);
                            newDialogState.getDiscourseUnitHypothesisMap().put(predecessorId, currentDu);
                            newDialogState.misunderstandingCounter = 0;

                            currentDu.actionAnalysis.update(yodaEnvironment, currentDu);
                            Double score = Utils.discourseUnitContextProbability(newDialogState, currentDu) * sluScore *
                                    groundedHypotheses.getRight().get(groundedDuKey);
                            resultHypotheses.put(newDialogState, score);
                        }
                    }
                }
            }
        } else { // if turn.speaker.equals("system")
            //todo: implement
        }
        return resultHypotheses;
    }

}
