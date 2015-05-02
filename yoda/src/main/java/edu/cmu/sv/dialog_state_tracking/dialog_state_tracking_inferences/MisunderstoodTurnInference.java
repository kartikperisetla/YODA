package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after misunderstanding a user turn, or after the system speaks an awkward turn
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
//        } else { // turn.speaker == system
//
//            DiscourseUnit newDUHypothesis = new DiscourseUnit();
//            SemanticsModel newSpokenByMeHypothesis = turn.getSystemUtterance();
//            newSpokenByMeHypothesis.getInternalRepresentation().put("dialogAct", duString);
//            //new SemanticsModel("{\"dialogAct\":\""+duString+"\"}");
//            newDUHypothesis.timeOfLastActByMe = timeStamp;
//            newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
//            newDUHypothesis.groundInterpretation = newSpokenByMeHypothesis;
//            newDUHypothesis.initiator = turn.speaker;
//            DialogState newDialogState = currentState.deepCopy();
//            newDialogState.discourseUnitCounter += 1;
//            newDialogState.getDiscourseUnitHypothesisMap().
//                    put("du_" + newDialogState.discourseUnitCounter, newDUHypothesis);
//
////            newDUHypothesis.actionAnalysis.update(yodaEnvironment, newDUHypothesis);
//            newDialogState.misunderstandingCounter ++;
//            resultHypotheses.put(newDialogState, probabilitySystemTurnMisunderstood);


        }
        return resultHypotheses;
    }
}
