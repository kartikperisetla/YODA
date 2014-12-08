package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by David Cohen on 9/19/14.
 */
public class DialogStateTracker implements Runnable {
    private static Logger logger = Logger.getLogger("yoda.dialog_state_tracking.DialogStateTracker");
    private static FileHandler fh;
    static {
        try {
            fh = new FileHandler("DialogStateTracker.log");
            fh.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        logger.addHandler(fh);
    }


    static Set<Class <? extends DialogStateUpdateInference>> updateInferences;
    static {
        updateInferences = new HashSet<>();
        updateInferences.add(PresentInference.class);
        updateInferences.add(AnswerInference.class);
        updateInferences.add(GroundingSuggestionInference.class);
        updateInferences.add(ConfirmGroundingSuggestionInference.class);
    }

    YodaEnvironment yodaEnvironment;
    Map<String, DialogStateHypothesis> hypothesisMap;
    StringDistribution hypothesisDistribution;

    public DialogStateTracker(YodaEnvironment yodaEnvironment){
        this.yodaEnvironment = yodaEnvironment;
        hypothesisDistribution = new StringDistribution();
        hypothesisMap = new HashMap<>();
        hypothesisDistribution.put("initial_dialog_state_hypothesis", 1.0);
        hypothesisMap.put("initial_dialog_state_hypothesis", new DialogStateHypothesis());
        this.yodaEnvironment.DmInputQueue.add(new ImmutablePair<>(hypothesisMap, hypothesisDistribution));
    }

    private void updateDialogState(Turn turn, long timeStamp){
        try {
            logger.info("New turn:\n"+turn);
            // validate input
            if (turn.hypotheses != null) {
                for (SemanticsModel sm : turn.hypotheses.values()) {
                    sm.validateSLUHypothesis();
                }
            }
            int newDialogStateHypothesisCounter = 0;
            StringDistribution newHypothesisDistribution = new StringDistribution();
            Map<String, DialogStateHypothesis> newHypotheses = new HashMap<>();

            for (String currentDialogStateHypothesisID : hypothesisMap.keySet()) {
                StringDistribution tmpNewHypothesisDistribution = new StringDistribution();
                Map<String, DialogStateHypothesis> tmpNewHypotheses = new HashMap<>();
                int tmpNewDUHypothesisCounter = 0;

                // perform dialog state update inferences
                {
                    for (Class<? extends DialogStateUpdateInference> updateInferenceClass : updateInferences) {
                        Pair<Map<String, DialogStateHypothesis>, StringDistribution> inferredUpdatedState =
                                updateInferenceClass.newInstance().applyAll(
                                        yodaEnvironment, hypothesisMap.get(currentDialogStateHypothesisID), turn, timeStamp);
                        for (String tmpNewDstHypothesisId : inferredUpdatedState.getRight().keySet()) {
                            String newDstHypothesisId = "dialog_state_hyp_" + tmpNewDUHypothesisCounter++;
                            tmpNewHypothesisDistribution.put(newDstHypothesisId,
                                    inferredUpdatedState.getRight().get(tmpNewDstHypothesisId) *
                                            hypothesisDistribution.get(currentDialogStateHypothesisID));
                            tmpNewHypotheses.put(newDstHypothesisId, inferredUpdatedState.getLeft().get(tmpNewDstHypothesisId));
                        }
                    }
                }

                // ground and analyse
                {
                    for (String key : tmpNewHypotheses.keySet()) {
                        Pair<Map<String, DialogStateHypothesis>, StringDistribution> groundedAndAnalysedUpdatedState =
                                tmpNewHypotheses.get(key).groundAndAnalyse(yodaEnvironment);
                        for (String tmpNewDstHypothesisId : groundedAndAnalysedUpdatedState.getRight().keySet()) {
                            String newDstHypothesisId = "dialog_state_hyp_" + newDialogStateHypothesisCounter++;
                            newHypothesisDistribution.put(newDstHypothesisId,
                                    groundedAndAnalysedUpdatedState.getRight().get(tmpNewDstHypothesisId) *
                                    tmpNewHypothesisDistribution.get(key));
                            newHypotheses.put(newDstHypothesisId, groundedAndAnalysedUpdatedState.getLeft().get(tmpNewDstHypothesisId));
                        }
                    }
                }
            }

            // todo: replace with new flexible beam
            hypothesisDistribution = HypothesisSetManagement.keepNBestDistribution(newHypothesisDistribution, 10);

            hypothesisMap = new HashMap<>();
            for (String key : hypothesisDistribution.keySet()) {
                hypothesisMap.put(key, newHypotheses.get(key));
            }
            hypothesisDistribution.normalize();

            String topHyp = hypothesisDistribution.getTopHypothesis();
            logger.info("dst loop, number of active hypotheses:" + hypothesisMap.size());
            logger.info("dialog state distribution:"+hypothesisDistribution);
            logger.info("top dialog state hypothesis: (p=" + hypothesisDistribution.get(topHyp) + ")");
            logger.info(hypothesisMap.get(topHyp).toString());

            yodaEnvironment.DmInputQueue.add(new ImmutablePair<>(hypothesisMap, hypothesisDistribution));

//        System.out.println("End of DialogStateTracker2.updateDialogStateTurn. discourseUnit.hypotheses:\n");
//        for (DiscourseUnit2.DialogStateHypothesis hyp : discourseUnit.hypotheses.values()){
//            System.out.println(hyp+"\n");
//        }
        } catch (IllegalAccessException | InstantiationException e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                Pair<Turn, Long> DstInput = yodaEnvironment.DstInputQueue.poll(1000, TimeUnit.MILLISECONDS);
                if (DstInput!=null) {
                    updateDialogState(DstInput.getKey(), DstInput.getValue());
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
