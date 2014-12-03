package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after a question is answered.
 *
 */
public class AnswerInference implements DialogStateUpdateInference {
    private final static double penaltyForReinterpretingFragment = .75;

    @Override
    public Pair<Map<String, DialogStateHypothesis>, StringDistribution> applyAll(
            DialogStateHypothesis currentState, Turn turn, long timeStamp) {

        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogStateHypothesis> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")){
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()){
                String dialogAct = turn.hypotheses.get(sluHypothesisID).getSlotPathFiller("dialogAct");
                if (Arrays.asList(Accept.class.getSimpleName(), Reject.class.getSimpleName(), DontKnow.class.getSimpleName()).
                        contains(dialogAct)) {

                    for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()){
                        DiscourseUnitHypothesis predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                        if (predecessor.initiator.equals("user"))
                            continue;
                        String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                        if (!Arrays.asList(YNQuestion.class.getSimpleName()).contains(predecessorDialogAct))
                            continue;
                        
                    }



                    String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                    DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                    DiscourseUnitHypothesis newDUHypothesis = new DiscourseUnitHypothesis();
                    SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                    resultDistribution.put(newDialogStateHypothesisID, penaltyForReinterpretingFragment);
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    newDUHypothesis.initiator = turn.speaker;
                    newDialogStateHypothesis.discourseUnitCounter += 1;
                    newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                            put("du_" + newDialogStateHypothesis.discourseUnitCounter, newDUHypothesis);
                    resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
                }
            }
        } else { // if turn.speaker.equals("system")
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (Arrays.asList(Accept.class.getSimpleName(), Reject.class.getSimpleName(), DontKnow.class.getSimpleName()).
                    contains(dialogAct)) {

            }

            String newDialogStateHypothesisID = "dialog_state_hyp_0";
            DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
            DiscourseUnitHypothesis newDUHypothesis = new DiscourseUnitHypothesis();
            SemanticsModel newSpokenByMeHypothesis = turn.systemUtterance.deepCopy();
            resultDistribution.put(newDialogStateHypothesisID, 1.0);
            newDUHypothesis.timeOfLastActByMe = timeStamp;
            newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
            newDUHypothesis.groundTruth = turn.groundedSystemMeaning;
            newDUHypothesis.initiator = turn.speaker;
            newDialogStateHypothesis.discourseUnitCounter += 1;
            newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                    put("du_"+newDialogStateHypothesis.discourseUnitCounter, newDUHypothesis);
            resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
        }

        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }

    Set<DiscourseUnitHypothesis> possiblePredecessors(DialogStateHypothesis currentState, String respondingSpeaker){
        for ()
    }

}
