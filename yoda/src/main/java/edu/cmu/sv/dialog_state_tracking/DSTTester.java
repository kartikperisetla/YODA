package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by David Cohen on 9/17/14.
 *
 * A class to run tests on the dialog state tracker
 *
 */
public class DSTTester {
    Map<Turn, Float> turns = new HashMap<>();
    Map<EvaluationState, Float> evaluationStates = new HashMap<>();

    /*
    * Turn contains an SLU result / SLU ground truth for a single user/system turn
    * */
    public static class Turn{
        Map<String, SemanticsModel> hypotheses;
        SemanticsModel systemUtterance;
        StringDistribution hypothesisDistribution;
        String speaker;

        public Turn(String speaker, SemanticsModel systemUtterance, Map<String, SemanticsModel> hypotheses, StringDistribution hypothesisDistribution) {
            this.hypotheses = hypotheses;
            this.systemUtterance = systemUtterance;
            this.hypothesisDistribution = hypothesisDistribution;
            this.speaker = speaker;
        }
    }

    /*
    * EvaluationState Describes a single dialog state,
    * which includes what has been spoken and understood by the user
    * and what has been spoken by the system.
    *
    * The class includes a method to evaluate a DU2 according to its resemblance to a desired EvaluationState
    * */
    public static class EvaluationState{
        SemanticsModel spokenByThem;
        SemanticsModel understoodByThem;
        SemanticsModel spokenByMe;

        public Pair<Integer, Double> evaluate(DiscourseUnit2 DU){
            int rank = -1;
            double relativeLikelihood = 0.0;
            double topLikelihood = DU.getHypothesisDistribution().
                    get(DU.getHypothesisDistribution().getTopHypothesis());
            List<String> sortedHypotheses = DU.getHypothesisDistribution().sortedHypotheses();
            for (int i = 0; i < sortedHypotheses.size(); i++) {
                String DUHypothesisID = sortedHypotheses.get(i);
                if (DU.getSpokenByThem().get(DUHypothesisID).equals(spokenByThem) &&
                        DU.getUnderstoodByThem().get(DUHypothesisID).equals(understoodByThem) &&
                        DU.getSpokenByMe().equals(spokenByMe)) {
                    rank = i;
                    relativeLikelihood = topLikelihood * 1.0 / DU.getHypothesisDistribution().get(DUHypothesisID);
                    break;
                }
            }
            return new ImmutablePair<>(rank, relativeLikelihood);
        }

        public EvaluationState(SemanticsModel spokenByThem, SemanticsModel understoodByThem, SemanticsModel spokenByMe) {
            this.spokenByThem = spokenByThem;
            this.understoodByThem = understoodByThem;
            this.spokenByMe = spokenByMe;
        }

        public SemanticsModel getSpokenByThem() {
            return spokenByThem;
        }

        public void setSpokenByThem(SemanticsModel spokenByThem) {
            this.spokenByThem = spokenByThem;
        }

        public SemanticsModel getUnderstoodByThem() {
            return understoodByThem;
        }

        public void setUnderstoodByThem(SemanticsModel understoodByThem) {
            this.understoodByThem = understoodByThem;
        }

        public SemanticsModel getSpokenByMe() {
            return spokenByMe;
        }

        public void setSpokenByMe(SemanticsModel spokenByMe) {
            this.spokenByMe = spokenByMe;
        }
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
        DiscourseUnit2 DU = new DiscourseUnit2();
        Float startTime = turns.values().stream().min(Float::compare).orElse((float)0)-1;
        Float endTime = turns.values().stream().max(Float::compare).orElse((float)0)+1;
        List<Integer> correctHypothesisRanks = new LinkedList<>();
        List<Double> correctHypothesisRelativeLikelihoods = new LinkedList<>();

        Set<Turn> turnsDone = new HashSet<>();
        Set<EvaluationState> evaluationStatesDone = new HashSet<>();
        // .1 second increments
        for (Float t = startTime; t < endTime; t+=(float).1) {
            for (Turn turn : turns.keySet()){
                if (turns.get(turn) < t && !turnsDone.contains(turn)){
                    DU.updateDiscourseUnit(turn.hypotheses, turn.systemUtterance,
                            turn.hypothesisDistribution,
                            turn.speaker, turns.get(turn));
                    turnsDone.add(turn);
                }
            }
            for (EvaluationState groundTruth : evaluationStates.keySet()){
                if (evaluationStates.get(groundTruth) < t && !evaluationStatesDone.contains(groundTruth)){
                    Pair<Integer, Double> result = groundTruth.evaluate(DU);
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

    public Map<EvaluationState, Float> getEvaluationStates() {
        return evaluationStates;
    }
}
