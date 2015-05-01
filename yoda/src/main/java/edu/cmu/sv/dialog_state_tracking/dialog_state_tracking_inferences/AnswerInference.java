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

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")){

        } else { // if turn.speaker.equals("system")
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            System.err.println("AnswerInference: here 1");
            if (Arrays.asList(
                    Accept.class.getSimpleName(),
                    Reject.class.getSimpleName(),
                    DontKnow.class.getSimpleName(),
                    Statement.class.getSimpleName(),
                    SearchReturnedNothing.class.getSimpleName()).contains(dialogAct)) {
                System.err.println("AnswerInference: here 2");
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    System.err.println("AnswerInference: here 2.5");
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    try {
                        Assert.verify(!predecessor.initiator.equals("system"));
                        System.err.println("AnswerInference: here a");
                        String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                        System.err.println("predecessor dialog act:" + predecessorDialogAct);
                        Assert.verify(Arrays.asList(WHQuestion.class.getSimpleName(), YNQuestion.class.getSimpleName()).contains(predecessorDialogAct));
                    } catch (Assert.AssertException e){
                        continue;
                    }
                    System.err.println("AnswerInference: here 3");
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
                    ans.put(newDialogState, Utils.discourseUnitContextProbability(newDialogState, predecessor));
                }

            }

        }
        return ans;
    }

}
