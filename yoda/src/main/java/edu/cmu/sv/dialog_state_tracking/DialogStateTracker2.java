package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.StringDistribution;
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
        updateInferences.add(SuggestedInference.class);
        updateInferences.add(AcknowledgeInference.class);
    }

    YodaEnvironment yodaEnvironment;
    DiscourseUnit2 discourseUnit;

    public DialogStateTracker2(YodaEnvironment yodaEnvironment){
        this.yodaEnvironment = yodaEnvironment;
        discourseUnit = new DiscourseUnit2();
        discourseUnit.hypothesisDistribution.put("initial_hypothesis", 1.0);
        discourseUnit.hypotheses.put("initial_hypothesis", new DiscourseUnit2.DiscourseUnitHypothesis());
        this.yodaEnvironment.DmInputQueue.add(discourseUnit);
    }

    private DiscourseUnit2 getDiscourseUnit(){return discourseUnit;}

    public void setDiscourseUnit(DiscourseUnit2 discourseUnit) {
        this.discourseUnit = discourseUnit;
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
            Map<String, DiscourseUnit2.DiscourseUnitHypothesis> newHypotheses = new HashMap<>();

            for (String currentDialogStateHypothesisID : discourseUnit.getHypothesisDistribution().keySet()) {
                for (Class<? extends DiscourseUnitUpdateInference> updateInferenceClass : updateInferences) {
                    DiscourseUnit2 inferredUpdatedState = updateInferenceClass.newInstance().
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

            for (String key : discourseUnit.getHypotheses().keySet()){
                discourseUnit.getHypotheses().get(key).groundAndAnalyse(yodaEnvironment);
            }

            discourseUnit.hypothesisDistribution = HypothesisSetManagement.keepNBestDistribution(newHypothesisDistribution,
                    DiscourseUnit2.BEAM_WIDTH);
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

    /**
     * A class to run tests on the dialog state tracker
     */
    public static class DSTTester {
        YodaEnvironment yodaEnvironment;
        Map<Turn, Long> turns = new HashMap<>();
        Map<DiscourseUnit2.DiscourseUnitHypothesis, Long> evaluationStates = new HashMap<>();

        public DSTTester(YodaEnvironment yodaEnvironment) {
            this.yodaEnvironment = yodaEnvironment;
        }

        /*
        * EvaluationResult contains the important results of an evaluation
        * */
        public class EvaluationResult{
            List<Integer> correctHypothesisRanks;
            List<Double> correctHypothesisRelativeLikelihoods;

            public EvaluationResult(List<Integer> correctHypothesisRanks, List<Double> correctHypothesisRelativeLikelihoods) {
                this.correctHypothesisRanks = correctHypothesisRanks;
                this.correctHypothesisRelativeLikelihoods = correctHypothesisRelativeLikelihoods;
            }

            public List<Integer> getCorrectHypothesisRanks() {
                return correctHypothesisRanks;
            }

            public void setCorrectHypothesisRanks(List<Integer> correctHypothesisRanks) {
                this.correctHypothesisRanks = correctHypothesisRanks;
            }

            public List<Double> getCorrectHypothesisRelativeLikelihoods() {
                return correctHypothesisRelativeLikelihoods;
            }

            public void setCorrectHypothesisRelativeLikelihoods(List<Double> correctHypothesisRelativeLikelihoods) {
                this.correctHypothesisRelativeLikelihoods = correctHypothesisRelativeLikelihoods;
            }

            @Override
            public String toString() {
                return "EvaluationResult{" +
                        "\ncorrectHypothesisRanks=" + correctHypothesisRanks +
                        "\ncorrectHypothesisRelativeLikelihoods=" + correctHypothesisRelativeLikelihoods +
                        '}';
            }
        }

        public EvaluationResult evaluate(){
            Long startTime = turns.values().stream().min(Long::compare).orElse((long) 0)-1;
            Long endTime = turns.values().stream().max(Long::compare).orElse((long) 0)+1;
            List<Integer> correctHypothesisRanks = new LinkedList<>();
            List<Double> correctHypothesisRelativeLikelihoods = new LinkedList<>();

            Set<Turn> turnsDone = new HashSet<>();
            Set<DiscourseUnit2.DiscourseUnitHypothesis> evaluationStatesDone = new HashSet<>();
            // .1 second increments
            for (Long t = startTime; t < endTime; t+=100) {
                for (Turn turn : turns.keySet()){
                    if (turns.get(turn) < t && !turnsDone.contains(turn)) {
                        yodaEnvironment.dst.updateDialogState(turn, t);
                        turnsDone.add(turn);
                    }
                }
                for (DiscourseUnit2.DiscourseUnitHypothesis groundTruth : evaluationStates.keySet()){
                    if (evaluationStates.get(groundTruth) < t && !evaluationStatesDone.contains(groundTruth)){
                        Pair<Integer, Double> result = yodaEnvironment.dst.getDiscourseUnit().compareHypothesis(groundTruth);
                        correctHypothesisRanks.add(result.getLeft());
                        correctHypothesisRelativeLikelihoods.add(result.getRight());
                        evaluationStatesDone.add(groundTruth);
                    }
                }
            }

            return new EvaluationResult(correctHypothesisRanks, correctHypothesisRelativeLikelihoods);
        }

        public Map<Turn, Long> getTurns() {
            return turns;
        }

        public Map<DiscourseUnit2.DiscourseUnitHypothesis, Long> getEvaluationStates() {
            return evaluationStates;
        }
    }
}
