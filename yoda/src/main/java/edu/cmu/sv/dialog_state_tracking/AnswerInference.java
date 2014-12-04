package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.utils.StringDistribution;
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
public class AnswerInference extends DialogStateUpdateInference {

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

                    for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                        DiscourseUnitHypothesis predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                        if (predecessor.initiator.equals("user"))
                            continue;
                        String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                        if (!Arrays.asList(YNQuestion.class.getSimpleName()).contains(predecessorDialogAct))
                            continue;
                        String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                        DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                        DiscourseUnitHypothesis newDUHypothesis = new DiscourseUnitHypothesis();

                        SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                        newDUHypothesis.timeOfLastActByThem = timeStamp;
                        newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                        newDUHypothesis.initiator = turn.speaker;
                        newDialogStateHypothesis.discourseUnitCounter += 1;
                        String newDiscourseUnitId = "du_" + newDialogStateHypothesis.discourseUnitCounter;
                        newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                                put(newDiscourseUnitId, newDUHypothesis);
                        newDialogStateHypothesis.getArgumentationLinks().add(
                                new DialogStateHypothesis.ArgumentationLink(predecessorId, newDiscourseUnitId));
                        resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
                        resultDistribution.put(newDialogStateHypothesisID,
                                Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(predecessor, newDialogStateHypothesis, "system")));
                    }
                }
            }
        } else { // if turn.speaker.equals("system")
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (Arrays.asList(Accept.class.getSimpleName(), Reject.class.getSimpleName(), DontKnow.class.getSimpleName()).
                    contains(dialogAct)) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnitHypothesis predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    if (predecessor.initiator.equals("system"))
                        continue;
                    String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                    if (!Arrays.asList(YNQuestion.class.getSimpleName()).contains(predecessorDialogAct))
                        continue;
                    String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;

                    DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                    DiscourseUnitHypothesis newDUHypothesis = new DiscourseUnitHypothesis();
                    SemanticsModel newSpokenByMeHypothesis = turn.systemUtterance.deepCopy();
                    newDUHypothesis.timeOfLastActByMe = timeStamp;
                    newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
                    newDUHypothesis.groundTruth = turn.groundedSystemMeaning;
                    newDUHypothesis.initiator = turn.speaker;
                    newDialogStateHypothesis.discourseUnitCounter += 1;
                    String newDiscourseUnitId = "du_" + newDialogStateHypothesis.discourseUnitCounter;
                    newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                            put(newDiscourseUnitId, newDUHypothesis);
                    newDialogStateHypothesis.getArgumentationLinks().add(
                            new DialogStateHypothesis.ArgumentationLink(predecessorId, newDiscourseUnitId));
                    resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
                    resultDistribution.put(newDialogStateHypothesisID,
                            Math.pow(.1, Utils.numberOfIntermediateDiscourseUnitsBySpeaker(predecessor, newDialogStateHypothesis, "user")));
                }

            }

        }

        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }

}
