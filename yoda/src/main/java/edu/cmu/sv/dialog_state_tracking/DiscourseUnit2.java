package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 9/17/14.
 */
public class DiscourseUnit2 {
    static final int BEAM_WIDTH = 10;
    StringDistribution hypothesisDistribution;
    Map<String, DiscourseUnitHypothesis> hypotheses;

    /*
    * The GroundedDiscourseUnitHypotheses class represents the list of grounded interpretations for a DU hypothesis
    * and their corresponding analysis
    * */
    public static class GroundedDiscourseUnitHypotheses {
        Map<String, SemanticsModel> groundedHypotheses;
        StringDistribution groundedHypothesesDistribution;

        public GroundedDiscourseUnitHypotheses(Map<String, SemanticsModel> groundedHypotheses, StringDistribution groundedHypothesesDistribution) {
            this.groundedHypotheses = groundedHypotheses;
            this.groundedHypothesesDistribution = groundedHypothesesDistribution;
        }

        // analysis
        Map<String, Double> ynqTruth;
        Map<String, Map<String, Double>> whqTruth;

        // todo: run the queries and fill in the analysis information
        public void analyse(){
        }
    }

    public static class GroundTruthDiscourseUnit {
        SemanticsModel groundTruth;
        public GroundTruthDiscourseUnit(SemanticsModel groundTruth) {
            this.groundTruth = groundTruth;
        }
    }


    public static class DiscourseUnitHypothesis {
        SemanticsModel spokenByMe;
        SemanticsModel spokenByThem;
        Long timeOfLastActByThem;
        Long timeOfLastActByMe;
        String initiator;
        GroundedDiscourseUnitHypotheses gnd; // if other-initiated
        GroundTruthDiscourseUnit gndTruth; // if self-initiated

        public DiscourseUnitHypothesis() {
            spokenByMe = new SemanticsModel("{\"dialogAct\":null, \"verb\":{}}");
            spokenByThem = new SemanticsModel("{\"dialogAct\":null, \"verb\":{}}");
            timeOfLastActByThem = null;
            timeOfLastActByMe = null;
        }

        public SemanticsModel getMostRecent() {
            if (timeOfLastActByMe==null && timeOfLastActByThem==null)
                return null;
            if (timeOfLastActByMe==null)
                return spokenByThem;
            if (timeOfLastActByThem==null)
                return spokenByMe;
            if (timeOfLastActByThem <= timeOfLastActByMe){
                return spokenByMe;
            }
            return spokenByThem;
        }

        public SemanticsModel getSpokenByMe() {
            return spokenByMe;
        }

        public void setSpokenByMe(SemanticsModel spokenByMe) {
            this.spokenByMe = spokenByMe;
            if (spokenByThem==null)
                initiator = "system";
        }

        public SemanticsModel getSpokenByThem() {
            return spokenByThem;
        }

        public void setSpokenByThem(SemanticsModel spokenByThem) {
            this.spokenByThem = spokenByThem;
            if (spokenByMe==null)
                initiator = "user";
        }

        @Override
        public String toString() {
            return "DialogStateHypothesis{" +
                    "\ntimeOfLastActByThem=" + timeOfLastActByThem +
                    ", timeOfLastActByMe=" + timeOfLastActByMe +
                    "\nspokenByMe=" + spokenByMe +
                    "\nspokenByThem=" + spokenByThem +
                    '}';
        }

        public void groundAndAnalyse(){
            if (initiator.equals("user")) {
                // compute the reference resolution marginals
                Map<String, StringDistribution> referenceResolutionMarginalHypotheses;


                // use the marginals to generate an n-best list of grounded hypotheses
                Map<String, SemanticsModel> nBestHypotheses = new HashMap<>();
                StringDistribution nBestDistribution = new StringDistribution();
                // todo: generate the n-best list
                gnd = new GroundedDiscourseUnitHypotheses(nBestHypotheses, nBestDistribution);

                // compute the analysis of each grounded hypothesis
                gnd.analyse();
            }
        }

    }

    public DiscourseUnit2() {
        hypothesisDistribution = new StringDistribution();
        hypotheses = new HashMap<>();
    }

    public StringDistribution getHypothesisDistribution() {
        return hypothesisDistribution;
    }

    public Map<String, DiscourseUnitHypothesis> getHypotheses() {
        return hypotheses;
    }

    public void setHypothesisDistribution(StringDistribution hypothesisDistribution) {
        this.hypothesisDistribution = hypothesisDistribution;
    }

    /*
        * Compare a hypothesis to the actual content of this discourse unit
        * */
    public Pair<Integer, Double> compareHypothesis(DiscourseUnitHypothesis testCase){

        int rank = -1;
        double relativeLikelihood = 0.0;
        double topLikelihood = hypothesisDistribution.get(hypothesisDistribution.getTopHypothesis());
        List<String> sortedHypotheses = hypothesisDistribution.sortedHypotheses();
        for (int i = 0; i < sortedHypotheses.size(); i++) {
            String DUHypothesisID = sortedHypotheses.get(i);
            DiscourseUnitHypothesis hypothesis = hypotheses.get(DUHypothesisID);

            if (hypothesis.spokenByThem.equals(testCase.spokenByThem) &&
                    hypothesis.spokenByMe.equals(testCase.spokenByMe)) {
                rank = i;
                relativeLikelihood = hypothesisDistribution.get(DUHypothesisID) * 1.0 / topLikelihood;
                break;
            }
        }
        return new ImmutablePair<>(rank, relativeLikelihood);
    }


}
