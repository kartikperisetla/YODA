package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences.DialogLostInference;
import edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences.DialogStateUpdateInference;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.InformDialogLost;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after a question is answered.
 *
 */
public class ResetLostDialogInference extends DialogStateUpdateInference {

    @Override
    public NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {

        NBestDistribution<DialogState> resultHypotheses = new NBestDistribution<>();

        if (turn.speaker.equals("system")) {
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (dialogAct.equals(InformDialogLost.class.getSimpleName())) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    try {
                        Assert.verify(!predecessor.initiator.equals("system"));
                        String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                        Assert.verify(predecessorDialogAct.equals(DialogLostInference.duString));
                    } catch (Assert.AssertException e){
                        continue;
                    }

                    DialogState newDialogState = new DialogState();
                    resultHypotheses.put(newDialogState, 1.0);
                }
            }
        }
        return resultHypotheses;
    }

}
