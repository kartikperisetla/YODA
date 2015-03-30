package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.semantics.SemanticsModel;
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
 * Infers that the system is completely confused about what is going on in the dialog
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
        }
        return resultHypotheses;
    }
}
