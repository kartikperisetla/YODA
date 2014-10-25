package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.YodaEnvironment;
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
        updateInferences.add(SuggestedInference.class);
        updateInferences.add(AcknowledgeInference.class);
    }

    YodaEnvironment yodaEnvironment;
    DiscourseUnit2 discourseUnit;

    public DialogStateTracker2(YodaEnvironment yodaEnvironment){
        this.yodaEnvironment = yodaEnvironment;
        discourseUnit = new DiscourseUnit2();
        discourseUnit.hypothesisDistribution.put("initial_hypothesis", 1.0);
        discourseUnit.hypotheses.put("initial_hypothesis", new DiscourseUnit2.DialogStateHypothesis());
    }

    public DiscourseUnit2 getDiscourseUnit(){return discourseUnit;}

    public void updateDialogState(Turn turn, float timeStamp) throws IllegalAccessException, InstantiationException {
        System.out.println("\n====== Turn ======");
        // validate input
        if (turn.hypotheses!=null) {
            for (SemanticsModel sm : turn.hypotheses.values()) {
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

                    // discard invalid DST hypotheses
                    try {
                        inferredUpdatedState.hypotheses.get(tmpNewDUHypothesisID).getSpokenByMe().validateDSTHypothesis();
                        inferredUpdatedState.hypotheses.get(tmpNewDUHypothesisID).getSpokenByThem().validateDSTHypothesis();
                    } catch (Error error){
                        System.out.println(error);
                        continue;
                    }

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
        discourseUnit.hypothesisDistribution.normalize();

        String topHyp = discourseUnit.hypothesisDistribution.getTopHypothesis();
        System.out.println("top dialog state hypothesis: (p="+discourseUnit.hypothesisDistribution.get(topHyp)+ ")");
        System.out.println(discourseUnit.hypotheses.get(topHyp));

//        System.out.println("End of DialogStateTracker2.updateDialogStateTurn. discourseUnit.hypotheses:\n");
//        for (DiscourseUnit2.DialogStateHypothesis hyp : discourseUnit.hypotheses.values()){
//            System.out.println(hyp+"\n");
//        }
    }
}
