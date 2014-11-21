package edu.cmu.sv.dialog_state_tracking;


import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by David Cohen on 9/17/14.
 *
 * A class to run tests on the dialog state tracker
 *
 */
public class DSTTester {
    YodaEnvironment yodaEnvironment;
    Map<Turn, Float> turns = new HashMap<>();
    Map<DiscourseUnit2.DialogStateHypothesis, Float> evaluationStates = new HashMap<>();

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
        Float startTime = turns.values().stream().min(Float::compare).orElse((float)0)-1;
        Float endTime = turns.values().stream().max(Float::compare).orElse((float)0)+1;
        List<Integer> correctHypothesisRanks = new LinkedList<>();
        List<Double> correctHypothesisRelativeLikelihoods = new LinkedList<>();

        Set<Turn> turnsDone = new HashSet<>();
        Set<DiscourseUnit2.DialogStateHypothesis> evaluationStatesDone = new HashSet<>();
        // .1 second increments
        for (Float t = startTime; t < endTime; t+=(float).1) {
            for (Turn turn : turns.keySet()){
                if (turns.get(turn) < t && !turnsDone.contains(turn)){
                    try {
                        yodaEnvironment.dst.updateDialogState(turn, t);
                        turnsDone.add(turn);
                    } catch (IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (DiscourseUnit2.DialogStateHypothesis groundTruth : evaluationStates.keySet()){
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

    public Map<Turn, Float> getTurns() {
        return turns;
    }

    public Map<DiscourseUnit2.DialogStateHypothesis, Float> getEvaluationStates() {
        return evaluationStates;
    }
}
