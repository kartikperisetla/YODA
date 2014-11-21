package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.parser.ParseException;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after an initial presentation.
 *
 */
public class PresentationInference implements DiscourseUnitUpdateInference {
    private final static double penaltyForReinterpretingFragment = .75;

    @Override
    public DiscourseUnit2 applyAll(DiscourseUnit2.DialogStateHypothesis currentState, Turn turn, long timeStamp) {
        int newDUHypothesisCounter = 0;
        DiscourseUnit2 ans = new DiscourseUnit2();
        // if this isn't the first utterance of a DU, this turn can't be interpreted using PresentInference
        if (currentState.timeOfLastActByMe!=null || currentState.timeOfLastActByThem!=null)
            return ans;

        if (turn.speaker.equals("user")){
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()){
                String dialogAct = turn.hypotheses.get(sluHypothesisID).getSlotPathFiller("dialogAct");

                // if the DA is a fragment, reinterpret it as any one of the discourseUnitDialogActs
                // with equal probability
                if (dialogAct.equals(Fragment.class.getSimpleName())){
                    for (Class<? extends DialogAct> newDAClass : DialogRegistry.discourseUnitDialogActs) {
                        String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                        DiscourseUnit2.DialogStateHypothesis newDUHypothesis =
                                new DiscourseUnit2.DialogStateHypothesis();
                        SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                        newSpokenByThemHypothesis.extendAndOverwrite(
                                new SemanticsModel("{\"dialogAct\":\""+newDAClass.getSimpleName()+"\"}"));
                        ans.getHypothesisDistribution().put(newDUHypothesisID, penaltyForReinterpretingFragment);
                        newDUHypothesis.timeOfLastActByThem = timeStamp;
                        newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                        ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);
                    }
                }
                // if the DA is one of the discourseUnitDialogActs, leave it alone
                else if (DialogRegistry.discourseUnitDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))){
                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    DiscourseUnit2.DialogStateHypothesis newDUHypothesis = new DiscourseUnit2.DialogStateHypothesis();
                    SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                    ans.getHypothesisDistribution().put(newDUHypothesisID, 1.0);
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);
                }
                // otherwise, this SLU hypothesis can't be interpreted using PresentInference
                else {}
            }
        } else { // if turn.speaker.equals("system")
            String newDUHypothesisID = "du_hyp_0";
            DiscourseUnit2.DialogStateHypothesis newDUHypothesis =
                    new DiscourseUnit2.DialogStateHypothesis();
            ans.getHypothesisDistribution().put(newDUHypothesisID, 1.0);
            newDUHypothesis.timeOfLastActByMe = timeStamp;
            newDUHypothesis.spokenByMe.extendAndOverwrite(turn.systemUtterance);
            ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);
        }


        return ans;
    }
}
