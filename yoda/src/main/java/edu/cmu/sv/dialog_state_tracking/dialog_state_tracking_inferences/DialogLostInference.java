package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.InformDialogLost;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers that the system is completely confused about what is going on in the dialog,
 * or infers that the dialog is reset once the system asks to start over
 *
 */
public class DialogLostInference extends DialogStateUpdateInference {
    public static final double maxLostProbability = .9;
    public static final String duString = "Lost";

    @Override
    public NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {

        NBestDistribution<DialogState> resultHypotheses = new NBestDistribution<>();

        if (turn.speaker.equals("user")) {
            // empty dialog state except for Lost DU
            DialogState newDialogState = new DialogState();
            DiscourseUnit newDUHypothesis = new DiscourseUnit();
            SemanticsModel newSpokenByThemHypothesis = new SemanticsModel("{\"dialogAct\":\""+duString+"\"}");
            newDUHypothesis.timeOfLastActByThem = timeStamp;
            newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
            newDUHypothesis.groundInterpretation = newSpokenByThemHypothesis;
            newDUHypothesis.initiator = turn.speaker;
            newDialogState.discourseUnitCounter += 1;
            newDialogState.getDiscourseUnitHypothesisMap().
                    put("du_" + newDialogState.discourseUnitCounter, newDUHypothesis);
            resultHypotheses.put(newDialogState,  maxLostProbability * (1.0 - Math.exp(-1*currentState.misunderstandingCounter)));

        } else if (turn.speaker.equals("system")) {
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
