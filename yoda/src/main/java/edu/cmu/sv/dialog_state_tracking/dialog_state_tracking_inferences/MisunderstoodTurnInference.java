package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.dialog_state_tracking.Utils;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestFixMisunderstanding;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after misunderstanding a user turn,
 * and after the system tells the user that the turn was misunderstood
 *
 */
public class MisunderstoodTurnInference extends DialogStateUpdateInference {
    public static final double probabilityUserTurnMisunderstood = .08;
//    public static final double probabilitySystemTurnMisunderstood = .0001;
    public static final String duString = "Misunderstood";

    @Override
    public NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {

        NBestDistribution<DialogState> resultHypotheses = new NBestDistribution<>();

        if (turn.speaker.equals("user")) {
            DiscourseUnit newDUHypothesis = new DiscourseUnit();
            SemanticsModel newSpokenByThemHypothesis = new SemanticsModel("{\"dialogAct\":\""+duString+"\"}");
            newDUHypothesis.timeOfLastActByThem = timeStamp;
            newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
            newDUHypothesis.groundInterpretation = newSpokenByThemHypothesis;
            newDUHypothesis.initiator = turn.speaker;
            DialogState newDialogState = currentState.deepCopy();
            newDialogState.discourseUnitCounter += 1;
            newDialogState.getDiscourseUnitHypothesisMap().
                    put("du_" + newDialogState.discourseUnitCounter, newDUHypothesis);

//            newDUHypothesis.actionAnalysis.update(yodaEnvironment, newDUHypothesis);
            newDialogState.misunderstandingCounter ++;
            resultHypotheses.put(newDialogState, probabilityUserTurnMisunderstood);

        } else if (turn.speaker.equals("system")) {
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (dialogAct.equals(RequestFixMisunderstanding.class.getSimpleName())) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    double contextAppropriateness = Utils.discourseUnitContextProbability(currentState, predecessor);

                    try {
                        Assert.verify(!predecessor.initiator.equals("system"));
                        String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                        Assert.verify(predecessorDialogAct.equals(MisunderstoodTurnInference.duString));
                    } catch (Assert.AssertException e){
                        continue;
                    }

                    DialogState newDialogState = currentState.deepCopy();
                    newDialogState.getDiscourseUnitHypothesisMap().remove(predecessorId);
                    resultHypotheses.put(newDialogState, contextAppropriateness);
                }
            }
        }
        return resultHypotheses;
    }
}
