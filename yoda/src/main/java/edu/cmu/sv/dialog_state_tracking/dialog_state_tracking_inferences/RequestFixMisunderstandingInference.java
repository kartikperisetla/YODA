package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.dialog_state_tracking.Utils;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestFixMisunderstanding;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after a question is answered.
 *
 */
public class RequestFixMisunderstandingInference extends DialogStateUpdateInference {

    @Override
    public NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {

        NBestDistribution<DialogState> ans = new NBestDistribution<>();

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

                    DialogState newDialogState = currentState.deepCopy();
                    newDialogState.getDiscourseUnitHypothesisMap().remove(predecessorId);
                    ans.put(newDialogState, Utils.discourseUnitContextProbability(
                                    currentState, currentState.getDiscourseUnitHypothesisMap().get(predecessorId)));
                }
            }
        }
        return ans;
    }

}
