package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
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
        int newDUHypothesisCounter = 0;
        StringDistribution newHypothesisDistribution = new StringDistribution();
        Map<String, SemanticsModel> newSpokenByThem = new HashMap<>();
        Map<String, SemanticsModel> newUnderstoodByThem = new HashMap<>();
        SemanticsModel newSpokenByMe = new SemanticsModel();

        for (String currentDialogStateHypothesisID : discourseUnit.getHypothesisDistribution().keySet()) {
            for (Class<? extends DiscourseUnitUpdateInference> updateInferenceClass : updateInferences) {
                DiscourseUnit2 inferredUpdatedState = updateInferenceClass.newInstance().
                        applyAll(discourseUnit, currentDialogStateHypothesisID, turn, timeStamp);
                for (String tmpNewDUHypothesisID : inferredUpdatedState.getHypothesisDistribution().keySet()){
                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    newHypothesisDistribution.put(newDUHypothesisID,
                            inferredUpdatedState.getHypothesisDistribution().get(tmpNewDUHypothesisID) *
                                    discourseUnit.getHypothesisDistribution().get(currentDialogStateHypothesisID));
                    newSpokenByThem.put(newDUHypothesisID,
                            inferredUpdatedState.getSpokenByThem().get(tmpNewDUHypothesisID));
                    newUnderstoodByThem.put(newDUHypothesisID,
                            inferredUpdatedState.getUnderstoodByThem().get(tmpNewDUHypothesisID));
                    if (newHypothesisDistribution.getTopHypothesis().equals(newDUHypothesisID)){
                        newSpokenByMe = inferredUpdatedState.getSpokenByMe();
                    }
                }
            }
        }
        List<String> hypothesesRemoved = newHypothesisDistribution.sortedHypotheses();
        if (hypothesesRemoved.size() > DiscourseUnit2.BEAM_WIDTH)
            hypothesesRemoved = hypothesesRemoved.subList(DiscourseUnit2.BEAM_WIDTH, hypothesesRemoved.size());
        else
            hypothesesRemoved = new LinkedList<>();
        hypothesesRemoved.stream().forEach(newHypothesisDistribution::remove);
        hypothesesRemoved.stream().forEach(newSpokenByThem::remove);
        hypothesesRemoved.stream().forEach(newUnderstoodByThem::remove);

        newHypothesisDistribution.normalize();
        discourseUnit.setHypothesisDistribution(newHypothesisDistribution);
        discourseUnit.setSpokenByThem(newSpokenByThem);
        discourseUnit.setUnderstoodByThem(newUnderstoodByThem);
        discourseUnit.setSpokenByMe(newSpokenByMe);
    }

}
