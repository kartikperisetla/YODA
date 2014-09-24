package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after an initial presentation.
 *
 */
public class PresentationInference implements DiscourseUnitUpdateInference {
    private final static double penaltyForReinterpretingFragment = .75;

    @Override
    public DiscourseUnit2 applyAll(DiscourseUnit2 DU, String assumedHypothesisID, Turn turn, float timeStamp) {
        int newDUHypothesisCounter = 0;
        final String initialHypothesisID = "initial_hypothesis";
        DiscourseUnit2 ans = new DiscourseUnit2();
        ans.clear();
        // if this isn't the first utterance of a DU, this turn can't be interpreted using PresentInference
        if (!DU.hypothesisDistribution.containsKey(initialHypothesisID))
            return ans;

        if (turn.speaker.equals("user")){
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()){
                String dialogAct = turn.hypotheses.get(sluHypothesisID).getSlots().get("dialogAct");

                // if the DA is a fragment, reinterpret it as any one of the discourseUnitDialogActs
                // with equal probability
                if (dialogAct.equals(Fragment.class.getSimpleName())){
                    for (Class<? extends DialogAct> newDAClass : DialogRegistry.discourseUnitDialogActs) {
                        String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                        SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                        newSpokenByThemHypothesis.getSlots().put("dialogAct", newDAClass.getSimpleName());
                        ans.getHypothesisDistribution().put(newDUHypothesisID, penaltyForReinterpretingFragment);
                        ans.getSpokenByThem().put(newDUHypothesisID, newSpokenByThemHypothesis);
                        ans.getUnderstoodByThem().put(newDUHypothesisID,
                                DU.getUnderstoodByThem().get(initialHypothesisID).deepCopy());
                    }
                }
                // if the DA is one of the discourseUnitDialogActs, leave it alone
                else if (DialogRegistry.discourseUnitDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))){
                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                    ans.getHypothesisDistribution().put(newDUHypothesisID, 1.0);
                    ans.getSpokenByThem().put(newDUHypothesisID, newSpokenByThemHypothesis);
                    ans.getUnderstoodByThem().put(newDUHypothesisID,
                            DU.getUnderstoodByThem().get(initialHypothesisID).deepCopy());
                }
                // otherwise, this SLU hypothesis can't be interpreted using PresentInference
                else {}

            }
        } else { // if turn.speaker.equals("system")
            String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
            ans.getHypothesisDistribution().put(newDUHypothesisID, 1.0);
            ans.getSpokenByThem().put(newDUHypothesisID, DU.getUnderstoodByThem().get(initialHypothesisID));
            SemanticsModel newUnderstoodByThem = DU.getUnderstoodByThem().get(initialHypothesisID).deepCopy();
            newUnderstoodByThem.extend(turn.systemUtterance);
            ans.getUnderstoodByThem().put(newDUHypothesisID, newUnderstoodByThem);
            ans.getSpokenByMe().extend(turn.systemUtterance);
        }


        return ans;
    }
}
