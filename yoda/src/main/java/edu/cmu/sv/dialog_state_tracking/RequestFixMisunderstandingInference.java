package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.DontKnow;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Reject;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestFixMisunderstanding;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after a question is answered.
 *
 */
public class RequestFixMisunderstandingInference extends DialogStateUpdateInference {

    @Override
    public Pair<Map<String, DialogState>, StringDistribution> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {

        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogState> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("system")) {
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (dialogAct.equals(RequestFixMisunderstanding.class.getSimpleName())) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    try {
                        Assert.verify(!predecessor.initiator.equals("system"));
                        String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                        Assert.verify(predecessorDialogAct.equals(MisunderstoodTurnInference.duString));
                    } catch (Assert.AssertException e){
                        continue;
                    }

                    String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                    DialogState newDialogState = currentState.deepCopy();
                    newDialogState.getDiscourseUnitHypothesisMap().remove(predecessorId);
                    resultHypotheses.put(newDialogStateHypothesisID, newDialogState);
                    resultDistribution.put(newDialogStateHypothesisID,
                            Utils.discourseUnitContextProbability(
                                    currentState, currentState.getDiscourseUnitHypothesisMap().get(predecessorId)));
                }
            }
        }
        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }

}