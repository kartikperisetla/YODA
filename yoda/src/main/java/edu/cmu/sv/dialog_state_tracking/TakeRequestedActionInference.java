package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 12/21/14.
 */
public class TakeRequestedActionInference extends DialogStateUpdateInference {
    @Override
    public Pair<Map<String, DialogState>, StringDistribution> applyAll(YodaEnvironment yodaEnvironment,
                                                                       DialogState currentState,
                                                                       Turn turn, long timeStamp) {
        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogState> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")){

        } else { // if turn.speaker.equals("system")
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (DialogRegistry.nonDialogTasks.contains(DialogRegistry.actionNameMap.get(dialogAct))) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);

                    try {
                        boolean anyMatchingSchema = false;
                        NonDialogTask thisTask = ((Class<? extends NonDialogTask>)
                                DialogRegistry.actionNameMap.get(dialogAct)).newInstance();
                        thisTask.setTaskSpec((JSONObject) turn.groundedSystemMeaning.newGetSlotPathFiller("verb"));

                        Assert.verify(!predecessor.initiator.equals("system"));
                        SemanticsModel resolvedMeaning = predecessor.getGroundInterpretation();
                        Assert.verify(resolvedMeaning != null);
                        for (ActionSchema actionSchema : DialogRegistry.actionSchemata) {
                            if (actionSchema.matchSchema(resolvedMeaning)) {
                                NonDialogTask enumeratedTask = actionSchema.applySchema(resolvedMeaning);
                                if (enumeratedTask.evaluationMatch(thisTask)) {
                                    anyMatchingSchema = true;
                                    break;
                                }
                            }
                        }
                        Assert.verify(anyMatchingSchema);
                    } catch (Assert.AssertException | InstantiationException | IllegalAccessException e){
                        continue;
                    }

                    String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;

                    DialogState newDialogState = currentState.deepCopy();
                    DiscourseUnit newDUHypothesis = new DiscourseUnit();
                    SemanticsModel newSpokenByMeHypothesis = turn.systemUtterance.deepCopy();
                    newDUHypothesis.timeOfLastActByMe = timeStamp;
                    newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
                    newDUHypothesis.groundTruth = turn.groundedSystemMeaning;
                    newDUHypothesis.initiator = turn.speaker;
                    newDialogState.discourseUnitCounter += 1;
                    String newDiscourseUnitId = "du_" + newDialogState.discourseUnitCounter;
                    newDialogState.getDiscourseUnitHypothesisMap().
                            put(newDiscourseUnitId, newDUHypothesis);
                    newDialogState.getArgumentationLinks().add(
                            new DialogState.ArgumentationLink(predecessorId, newDiscourseUnitId));
                    resultHypotheses.put(newDialogStateHypothesisID, newDialogState);
                    resultDistribution.put(newDialogStateHypothesisID,
                            Utils.discourseUnitContextProbability(newDialogState, predecessor));
                }

            }

        }

        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }
}
