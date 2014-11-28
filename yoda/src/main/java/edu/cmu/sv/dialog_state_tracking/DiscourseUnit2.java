package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.dialog_task.DialogTask;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
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

        // analysis results
        Map<String, Double> ynqTruth;
        Map<String, Map<String, Double>> whqTruth;

        public Map<String, Double> getYnqTruth() {
            return ynqTruth;
        }

        public void setYnqTruth(Map<String, Double> ynqTruth) {
            this.ynqTruth = ynqTruth;
        }

        public Map<String, SemanticsModel> getGroundedHypotheses() {
            return groundedHypotheses;
        }

        public void setGroundedHypotheses(Map<String, SemanticsModel> groundedHypotheses) {
            this.groundedHypotheses = groundedHypotheses;
        }

        public StringDistribution getGroundedHypothesesDistribution() {
            return groundedHypothesesDistribution;
        }

        public void setGroundedHypothesesDistribution(StringDistribution groundedHypothesesDistribution) {
            this.groundedHypothesesDistribution = groundedHypothesesDistribution;
        }

        @Override
        public String toString() {
            return "GroundedDiscourseUnitHypotheses{" +
                    "\ngroundedHypotheses=" + groundedHypotheses +
                    "\ngroundedHypothesesDistribution=" + groundedHypothesesDistribution +
                    "\nynqTruth=" + ynqTruth +
                    "\nwhqTruth=" + whqTruth +
                    '}';
        }
    }

    public static class DiscourseUnitHypothesis {
        SemanticsModel spokenByMe;
        SemanticsModel spokenByThem;
        Long timeOfLastActByThem;
        Long timeOfLastActByMe;
        String initiator;
        GroundedDiscourseUnitHypotheses gnd; // if other-initiated
        SemanticsModel gndTruth; // if self-initiated

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
                    "\ngnd=" + gnd +
                    '}';
        }

        public void groundAndAnalyse(YodaEnvironment yodaEnvironment){
            System.out.println("DU2.groundAndAnalyse");
            System.out.println("spokenByThem:"+spokenByThem);
            System.out.println("spokenByMe:"+spokenByMe);
            String dialogActString = (String) spokenByThem.newGetSlotPathFiller("dialogAct");
            Class<? extends DialogTask> taskClass = DialogRegistry.dialogTaskMap.
                    get(DialogRegistry.dialogActNameMap.get(dialogActString));
            try {
                gnd = taskClass.newInstance().ground(this, yodaEnvironment);
                 taskClass.newInstance().analyse(gnd, yodaEnvironment);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                System.exit(0);
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
