package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.database.ReferenceResolution;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * Created by David Cohen on 9/19/14.
 *
 * Infers the dialog state after an initial presentation.
 *
 */
public class PresentInference extends DialogStateUpdateInference {
    @Override
    public NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {

        NBestDistribution<DialogState> resultHypotheses = new NBestDistribution<>();

        if (turn.speaker.equals("user")){
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()){
                Double sluScore = turn.hypothesisDistribution.get(sluHypothesisID);
                String dialogAct = turn.hypotheses.get(sluHypothesisID).getSlotPathFiller("dialogAct");

                // if the DA is one of the discourseUnitDialogActs, leave it alone
                if (DialogRegistry.discourseUnitDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))){
                    DiscourseUnit newDUHypothesis = new DiscourseUnit();
                    SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    newDUHypothesis.initiator = turn.speaker;

                    Pair<Map<String, DiscourseUnit>, StringDistribution> groundedHypotheses =
                            ReferenceResolution.resolveDiscourseUnit(newDUHypothesis, yodaEnvironment);
                    for (String groundedDuKey: groundedHypotheses.getRight().keySet()){
                        DialogState newDialogState = currentState.deepCopy();
                        DiscourseUnit currentDu = groundedHypotheses.getLeft().get(groundedDuKey);
                        newDialogState.discourseUnitCounter += 1;
                        newDialogState.misunderstandingCounter = 0;
                        newDialogState.getDiscourseUnitHypothesisMap().
                                put("du_" + newDialogState.discourseUnitCounter, currentDu);
                        currentDu.actionAnalysis.update(yodaEnvironment, currentDu);
                        resultHypotheses.put(newDialogState, groundedHypotheses.getRight().get(groundedDuKey)*sluScore);
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
                DiscourseUnit newDUHypothesis = new DiscourseUnit();
                SemanticsModel newSpokenByMeHypothesis = turn.systemUtterance.deepCopy();
                newDUHypothesis.timeOfLastActByMe = timeStamp;
                newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
                newDUHypothesis.groundTruth = turn.groundedSystemMeaning;
                newDUHypothesis.initiator = turn.speaker;

                DialogState newDialogState = currentState.deepCopy();
                newDialogState.discourseUnitCounter += 1;
                newDialogState.getDiscourseUnitHypothesisMap().
                        put("du_" + newDialogState.discourseUnitCounter, newDUHypothesis);
                resultHypotheses.put(newDialogState, 1.0);
            }
        }

        return resultHypotheses;
    }
}
