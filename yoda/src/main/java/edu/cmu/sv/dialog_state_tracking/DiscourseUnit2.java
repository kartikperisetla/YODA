package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 9/17/14.
 */
public class DiscourseUnit2 {
    static final int BEAM_WIDTH = 10;
    StringDistribution hypothesisDistribution;
    Map<String, DialogStateHypothesis> hypotheses;

    public static class DialogStateHypothesis{
        SemanticsModel spokenByMe;
        SemanticsModel spokenByThem;
        Float timeOfLastActByThem;
        Float timeOfLastActByMe;

        public DialogStateHypothesis() {
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
        }

        public SemanticsModel getSpokenByThem() {
            return spokenByThem;
        }

        public void setSpokenByThem(SemanticsModel spokenByThem) {
            this.spokenByThem = spokenByThem;
        }

        public Float getTimeOfLastActByThem() {
            return timeOfLastActByThem;
        }

        public void setTimeOfLastActByThem(Float timeOfLastActByThem) {
            this.timeOfLastActByThem = timeOfLastActByThem;
        }

        public Float getTimeOfLastActByMe() {
            return timeOfLastActByMe;
        }

        public void setTimeOfLastActByMe(Float timeOfLastActByMe) {
            this.timeOfLastActByMe = timeOfLastActByMe;
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
    }

    public DiscourseUnit2() {
        hypothesisDistribution = new StringDistribution();
        hypotheses = new HashMap<>();
    }

    public StringDistribution getHypothesisDistribution() {
        return hypothesisDistribution;
    }

    public Map<String, DialogStateHypothesis> getHypotheses() {
        return hypotheses;
    }

    /*
    * Compare a hypothesis to the actual content of this discourse unit
    * */
    public Pair<Integer, Double> compareHypothesis(DialogStateHypothesis testCase){

        int rank = -1;
        double relativeLikelihood = 0.0;
        double topLikelihood = hypothesisDistribution.get(hypothesisDistribution.getTopHypothesis());
        List<String> sortedHypotheses = hypothesisDistribution.sortedHypotheses();
        for (int i = 0; i < sortedHypotheses.size(); i++) {
            String DUHypothesisID = sortedHypotheses.get(i);
            DialogStateHypothesis hypothesis = hypotheses.get(DUHypothesisID);

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
