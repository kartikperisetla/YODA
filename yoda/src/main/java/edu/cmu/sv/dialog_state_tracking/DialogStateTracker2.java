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
public class DialogStateTracker2 implements Runnable {
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


    static Set<Class <? extends DiscourseUnitUpdateInference>> updateInferences;
    static {
        updateInferences = new HashSet<>();
        updateInferences.add(PresentationInference.class);
//        updateInferences.add(SuggestedInference.class);
//        updateInferences.add(AcknowledgeInference.class);
    }

    YodaEnvironment yodaEnvironment;
    Map<String, DialogStateHypothesis> hypothesisMap;
    StringDistribution hypothesisDistribution;

    public DialogStateTracker2(YodaEnvironment yodaEnvironment){
        this.yodaEnvironment = yodaEnvironment;
        hypothesisDistribution = new StringDistribution();
        hypothesisMap = new HashMap<>();
        this.yodaEnvironment.DmInputQueue.add(new ImmutablePair<>(hypothesisMap, hypothesisDistribution));
    }

    private void updateDialogState(Turn turn, long timeStamp){
        try {
            logger.info("====== Turn ======");
            // validate input
            if (turn.hypotheses != null) {
                for (SemanticsModel sm : turn.hypotheses.values()) {
                    sm.validateSLUHypothesis();
                }
            }
            int newDUHypothesisCounter = 0;
            StringDistribution newHypothesisDistribution = new StringDistribution();
            Map<String, DiscourseUnitHypothesis.DiscourseUnitHypothesis> newHypotheses = new HashMap<>();

            for (String currentDialogStateHypothesisID : discourseUnit.getHypothesisDistribution().keySet()) {
                for (Class<? extends DiscourseUnitUpdateInference> updateInferenceClass : updateInferences) {
                    DiscourseUnitHypothesis inferredUpdatedState = updateInferenceClass.newInstance().
                            applyAll(discourseUnit.hypotheses.get(currentDialogStateHypothesisID), turn, timeStamp);
                    for (String tmpNewDUHypothesisID : inferredUpdatedState.getHypothesisDistribution().keySet()) {

                        // discard invalid DST hypotheses
                        try {
                            inferredUpdatedState.hypotheses.get(tmpNewDUHypothesisID).getSpokenByMe().validateDSTHypothesis();
                            inferredUpdatedState.hypotheses.get(tmpNewDUHypothesisID).getSpokenByThem().validateDSTHypothesis();
                        } catch (Error error) {
                            logger.info("discarding invalid DST hypothesis:"+ error.toString());
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
            logger.info("dst loop, number of active hypotheses:"+discourseUnit.getHypotheses().size());
            for (String key : newHypothesisDistribution.keySet()){
                newHypotheses.get(key).groundAndAnalyse(yodaEnvironment);
            }

            discourseUnit.hypothesisDistribution = HypothesisSetManagement.keepNBestDistribution(newHypothesisDistribution,
                    DiscourseUnitHypothesis.BEAM_WIDTH);
            discourseUnit.hypotheses = new HashMap<>();
            for (String key : discourseUnit.hypothesisDistribution.keySet()) {
                discourseUnit.hypotheses.put(key, newHypotheses.get(key));
            }
            discourseUnit.hypothesisDistribution.normalize();

            String topHyp = discourseUnit.hypothesisDistribution.getTopHypothesis();
            logger.info("top dialog state hypothesis: (p=" + discourseUnit.hypothesisDistribution.get(topHyp) + ")");
            logger.info(discourseUnit.hypotheses.get(topHyp).toString());

            yodaEnvironment.DmInputQueue.add(discourseUnit);

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
