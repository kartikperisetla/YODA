package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after an initial presentation.
 *
 */
public class PresentationInference implements DialogStateUpdateInference {
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

                // if the DA is a fragment, reinterpret it as any one of the discourseUnitDialogActs
                // with equal probability
                if (dialogAct.equals(Fragment.class.getSimpleName())){
                    for (Class<? extends DialogAct> newDAClass : DialogRegistry.discourseUnitDialogActs) {
                        String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                        DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                        DiscourseUnitHypothesis newDUHypothesis = new DiscourseUnitHypothesis();
                        SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                        newSpokenByThemHypothesis.extendAndOverwrite(
                                new SemanticsModel("{\"dialogAct\":\"" + newDAClass.getSimpleName() + "\"}"));
                        resultDistribution.put(newDialogStateHypothesisID, penaltyForReinterpretingFragment);
                        newDUHypothesis.timeOfLastActByThem = timeStamp;
                        newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                        newDUHypothesis.initiator = turn.speaker;
                        newDialogStateHypothesis.discourseUnitCounter += 1;
                        newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                                put("du_"+newDialogStateHypothesis.discourseUnitCounter, newDUHypothesis);
                        resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
                    }
                }
                // if the DA is one of the discourseUnitDialogActs, leave it alone
                else if (DialogRegistry.discourseUnitDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))){
                    String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                    DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                    DiscourseUnitHypothesis newDUHypothesis = new DiscourseUnitHypothesis();
                    SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                    resultDistribution.put(newDialogStateHypothesisID, 1.0);
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    newDUHypothesis.initiator = turn.speaker;
                    newDialogStateHypothesis.discourseUnitCounter += 1;
                    newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                            put("du_"+newDialogStateHypothesis.discourseUnitCounter, newDUHypothesis);
                    resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
                }
                // otherwise, this SLU hypothesis can't be interpreted using PresentInference
                else {}
            }
        } else { // if turn.speaker.equals("system")
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
}
