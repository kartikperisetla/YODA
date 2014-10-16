package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.StringDistribution;

import java.util.*;

/**
 * Created by David Cohen on 9/19/14.
 */
public class DialogStateTracker2 {
    static Set<Class <? extends DiscourseUnitUpdateInference>> updateInferences;
    static {
        updateInferences = new HashSet<>();
        updateInferences.add(PresentationInference.class);
    }

    DiscourseUnit2 discourseUnit;

    public DialogStateTracker2(){discourseUnit = new DiscourseUnit2();}

    public DiscourseUnit2 getDiscourseUnit(){return discourseUnit;}

    public void updateDialogState(Turn turn, float timeStamp) throws IllegalAccessException, InstantiationException {
        System.out.println("DST.updateDialogState");
        // validate input
        if (turn.hypotheses!=null) {
            for (SemanticsModel sm : turn.hypotheses.values()) {
                System.out.println(sm);
                sm.validateSLUHypothesis();
            }
        }
        int newDUHypothesisCounter = 0;
        StringDistribution newHypothesisDistribution = new StringDistribution();
        Map<String, DiscourseUnit2.DialogStateHypothesis> newHypotheses = new HashMap<>();

        for (String currentDialogStateHypothesisID : discourseUnit.getHypothesisDistribution().keySet()) {
            for (Class<? extends DiscourseUnitUpdateInference> updateInferenceClass : updateInferences) {
                DiscourseUnit2 inferredUpdatedState = updateInferenceClass.newInstance().
                        applyAll(discourseUnit.hypotheses.get(currentDialogStateHypothesisID), turn, timeStamp);
                for (String tmpNewDUHypothesisID : inferredUpdatedState.getHypothesisDistribution().keySet()){
                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;

                    newHypothesisDistribution.put(newDUHypothesisID,
                            inferredUpdatedState.getHypothesisDistribution().get(tmpNewDUHypothesisID) *
                                    discourseUnit.getHypothesisDistribution().get(currentDialogStateHypothesisID));

                    newHypotheses.put(newDUHypothesisID, inferredUpdatedState.hypotheses.get(tmpNewDUHypothesisID));
                }
            }
        }

        discourseUnit.hypothesisDistribution = HypothesisSetManagement.keepNBestDistribution(newHypothesisDistribution,
                DiscourseUnit2.BEAM_WIDTH);
        discourseUnit.hypotheses = new HashMap<>();
        for (String key : discourseUnit.hypothesisDistribution.keySet()){
            discourseUnit.hypotheses.put(key, newHypotheses.get(key));
        }
    }
}
