package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.dialog_state_tracking.Utils;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

/**
 * Created by David Cohen on 5/8/2015.
 *
 * Infers that the user has made an out-of-capability request
 */
public class OOCInference extends DialogStateUpdateInference {

    @Override
    public NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp) {

        NBestDistribution<DialogState> resultHypotheses = new NBestDistribution<>();

        if (turn.speaker.equals("user")) {
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()){
                Double sluScore = turn.hypothesisDistribution.get(sluHypothesisID);
                String dialogAct = turn.hypotheses.get(sluHypothesisID).getSlotPathFiller("dialogAct");

                if (DialogRegistry.oocDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))){
                    DiscourseUnit newDUHypothesis = new DiscourseUnit();
                    SemanticsModel newSpokenByThemHypothesis = turn.hypotheses.get(sluHypothesisID).deepCopy();
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    newDUHypothesis.initiator = turn.speaker;
                    newDUHypothesis.setGroundInterpretation(newSpokenByThemHypothesis.deepCopy());

                    DialogState newDialogState = currentState.deepCopy();
                    newDialogState.discourseUnitCounter += 1;
                    newDialogState.misunderstandingCounter = 0;
                    newDialogState.getDiscourseUnitHypothesisMap().
                            put("du_" + newDialogState.discourseUnitCounter, newDUHypothesis);

                    resultHypotheses.put(newDialogState, sluScore);
                }
            }
        } else if (turn.speaker.equals("system")) {
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            if (DialogRegistry.oocResponseDialogActs.contains(DialogRegistry.dialogActNameMap.get(dialogAct))) {
                for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                    DiscourseUnit predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                    try {
                        Assert.verify(!predecessor.initiator.equals("system"));
                        String predecessorDialogAct = predecessor.spokenByThem.getSlotPathFiller("dialogAct");
                        Assert.verify(DialogRegistry.dialogActNameMap.containsKey(predecessorDialogAct));
                        Assert.verify(DialogRegistry.oocDialogActs.contains(
                                DialogRegistry.dialogActNameMap.get(predecessorDialogAct)));
                    } catch (Assert.AssertException e){
                        continue;
                    }

                    DialogState newDialogState = currentState.deepCopy();
                    newDialogState.getDiscourseUnitHypothesisMap().remove(predecessorId);
                    resultHypotheses.put(newDialogState, Utils.discourseUnitContextProbability(
                            currentState, currentState.getDiscourseUnitHypothesisMap().get(predecessorId)));
                }
            }
        }
        return resultHypotheses;
    }
}
