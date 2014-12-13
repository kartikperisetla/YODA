package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.DontKnow;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Reject;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after an initial presentation.
 *
 */
public class PresentInference extends DialogStateUpdateInference {
    private final static double penaltyForReinterpretingFragment = .5;

    @Override
    public Pair<Map<String, DialogStateHypothesis>, StringDistribution> applyAll(
            YodaEnvironment yodaEnvironment, DialogStateHypothesis currentState, Turn turn, long timeStamp) {

        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogStateHypothesis> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")){
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()){
                String dialogAct = turn.hypotheses.get(sluHypothesisID).getSlotPathFiller("dialogAct");

                // if the DA is one of the discourseUnitDialogActs, leave it alone
                if (DialogRegistry.discourseUnitDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))){
                    DiscourseUnitHypothesis newDUHypothesis = new DiscourseUnitHypothesis();
                    SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    newDUHypothesis.initiator = turn.speaker;

                    Pair<Map<String, DiscourseUnitHypothesis>, StringDistribution> groundedHypotheses =
                            newDUHypothesis.ground(yodaEnvironment);
                    for (String groundedDuKey: groundedHypotheses.getRight().keySet()){
                        String newDialogStateHypothesisID = "dialog_state_hyp_" + newHypothesisCounter++;
                        DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                        newDialogStateHypothesis.discourseUnitCounter += 1;
                        newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                                put("du_"+newDialogStateHypothesis.discourseUnitCounter, groundedHypotheses.getLeft().get(groundedDuKey));
                        newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                                get("du_"+newDialogStateHypothesis.discourseUnitCounter).analyse(yodaEnvironment);
                        resultDistribution.put(newDialogStateHypothesisID, groundedHypotheses.getRight().get(groundedDuKey));
                        resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
                    }

                } else if (dialogAct.equals(Fragment.class.getSimpleName())){
                    // todo: implement re-interpret fragment as a discourse unit act
                }
                // otherwise, this SLU hypothesis can't be interpreted using PresentInference
                else {}
            }
        } else { // if turn.speaker.equals("system")
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (DialogRegistry.discourseUnitDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))) {
                String newDialogStateHypothesisID = "dialog_state_hyp_0";
                DiscourseUnitHypothesis newDUHypothesis = new DiscourseUnitHypothesis();
                SemanticsModel newSpokenByMeHypothesis = turn.systemUtterance.deepCopy();
                newDUHypothesis.timeOfLastActByMe = timeStamp;
                newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
                newDUHypothesis.groundTruth = turn.groundedSystemMeaning;
                newDUHypothesis.initiator = turn.speaker;

                DialogStateHypothesis newDialogStateHypothesis = currentState.deepCopy();
                newDialogStateHypothesis.discourseUnitCounter += 1;
                newDialogStateHypothesis.getDiscourseUnitHypothesisMap().
                        put("du_" + newDialogStateHypothesis.discourseUnitCounter, newDUHypothesis);
                resultDistribution.put(newDialogStateHypothesisID, 1.0);
                resultHypotheses.put(newDialogStateHypothesisID, newDialogStateHypothesis);
            }
        }

        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }
}
