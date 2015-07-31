package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.dialog_state_tracking.Utils;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

import java.util.Arrays;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after a question is answered.
 *
 */
public class AnswerInference extends DialogStateUpdateInference {

    @Override
    public NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {

        NBestDistribution<DialogState> ans = new NBestDistribution<>();

        if (turn.speaker.equals("user")){

        } else { // if turn.speaker.equals("system")
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (Arrays.asList(
                    Accept.class.getSimpleName(),
                    Reject.class.getSimpleName(),
                    DontKnow.class.getSimpleName(),
                    Statement.class.getSimpleName(),
                    SearchReturnedNothing.class.getSimpleName()).contains(dialogAct)) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    double contextAppropriateness = Utils.discourseUnitContextProbability(currentState, predecessor);

                    try {
                        Assert.verify(!predecessor.initiator.equals("system"));
                        String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                        Assert.verify(Arrays.asList(WHQuestion.class.getSimpleName(), YNQuestion.class.getSimpleName()).contains(predecessorDialogAct));
                    } catch (Assert.AssertException e){
                        continue;
                    }

                    DialogState newDialogState = currentState.deepCopy();
                    DiscourseUnit newDUHypothesis = new DiscourseUnit();
                    SemanticsModel newSpokenByMeHypothesis = turn.systemUtterance.deepCopy();
                    newDUHypothesis.timeOfLastActByMe = timeStamp;
                    newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
                    newDUHypothesis.groundTruth = turn.groundedSystemMeaning;
                    newDUHypothesis.initiator = turn.speaker;
                    newDialogState.discourseUnitCounter += 1;
                    newDialogState.misunderstandingCounter = 0;
                    String newDiscourseUnitId = "du_" + newDialogState.discourseUnitCounter;
                    newDialogState.getDiscourseUnitHypothesisMap().
                            put(newDiscourseUnitId, newDUHypothesis);
                    newDialogState.getArgumentationLinks().add(
                            new DialogState.ArgumentationLink(predecessorId, newDiscourseUnitId));
                    ans.put(newDialogState, contextAppropriateness);
                }

            }

        }
        return ans;
    }

}
