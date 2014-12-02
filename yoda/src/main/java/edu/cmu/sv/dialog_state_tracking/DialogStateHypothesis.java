package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.utils.Combination;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 12/1/14.
 */
public class DialogStateHypothesis {
    Map<String, DiscourseUnitHypothesis> discourseUnitHypothesisMap = new HashMap<>();
    Set<ArgumentationLink> acceptLinks = new HashSet<>();
    Set<ArgumentationLink> rejectLinks = new HashSet<>();
    long discourseUnitCounter = 0;

    public static class ArgumentationLink{
        public ArgumentationLink(String predecessor, String successor) {
            this.predecessor = predecessor;
            this.successor = successor;
        }

        String predecessor;
        String successor;

        public String getPredecessor() {
            return predecessor;
        }

        public void setPredecessor(String predecessor) {
            this.predecessor = predecessor;
        }

        public String getSuccessor() {
            return successor;
        }

        public void setSuccessor(String successor) {
            this.successor = successor;
        }
    }

    public DialogStateHypothesis deepCopy(){
        DialogStateHypothesis ans = new DialogStateHypothesis();
        for (ArgumentationLink link : acceptLinks)
            ans.acceptLinks.add(new ArgumentationLink(link.predecessor, link.successor));
        for (ArgumentationLink link : rejectLinks)
            ans.rejectLinks.add(new ArgumentationLink(link.predecessor, link.successor));
        for (String key : discourseUnitHypothesisMap.keySet()){
            ans.discourseUnitHypothesisMap.put(key, discourseUnitHypothesisMap.get(key).deepCopy());
        }
        ans.discourseUnitCounter = discourseUnitCounter;
        return ans;
    }

    public Pair<Map<String, DialogStateHypothesis>, StringDistribution> groundAndAnalyse(YodaEnvironment yodaEnvironment){
        Map<String, Set<Pair<DiscourseUnitHypothesis, Double>>> possibleDuGroundings = new HashMap<>();

        for (String duId : discourseUnitHypothesisMap.keySet()){
            Set<Pair<DiscourseUnitHypothesis, Double>> weightedGroundings = new HashSet<>();
            if (discourseUnitHypothesisMap.get(duId).getGroundInterpretation()!=null ||
                    discourseUnitHypothesisMap.get(duId).getGroundTruth()!=null){
                weightedGroundings.add(new ImmutablePair<>(discourseUnitHypothesisMap.get(duId), 1.0));
            } else {
                Pair<Map<String, DiscourseUnitHypothesis>, StringDistribution> groundedDuHypotheses =
                        discourseUnitHypothesisMap.get(duId).groundAndAnalyse(yodaEnvironment);
                for (String key : groundedDuHypotheses.getKey().keySet()){
                    weightedGroundings.add(new ImmutablePair<>(
                            groundedDuHypotheses.getKey().get(key), groundedDuHypotheses.getValue().get(key)));
                }
            }
            possibleDuGroundings.put(duId, weightedGroundings);
        }

        Set<Map<String, Pair<DiscourseUnitHypothesis, Double>>> possibleDuBindings = Combination.possibleBindings(possibleDuGroundings);
        Map<String, DialogStateHypothesis> outputHypotheses = new HashMap<>();
        StringDistribution outputDistribution = new StringDistribution();
        int i=0;
        for (Map<String, Pair<DiscourseUnitHypothesis, Double>> binding : possibleDuBindings){
            double hypothesisLikelihood = 1.0;
            DialogStateHypothesis ans = this.deepCopy(); // do a deep copy so that all links and counters are copied
            String dialogStateHypothesisId = "DialogStateHypothesis.groundAndAnalyse():dialog_state_"+i++;
            for (String key : binding.keySet()){
                ans.discourseUnitHypothesisMap.put(key, binding.get(key).getLeft().deepCopy());
                hypothesisLikelihood *= binding.get(key).getRight();
            }
            outputHypotheses.put(dialogStateHypothesisId, ans);
            outputDistribution.put(dialogStateHypothesisId, hypothesisLikelihood);
        }

        return new ImmutablePair<>(outputHypotheses, outputDistribution);
    }

    public Map<String, DiscourseUnitHypothesis> getDiscourseUnitHypothesisMap() {
        return discourseUnitHypothesisMap;
    }

    public void setDiscourseUnitHypothesisMap(Map<String, DiscourseUnitHypothesis> discourseUnitHypothesisMap) {
        this.discourseUnitHypothesisMap = discourseUnitHypothesisMap;
    }

    public Set<ArgumentationLink> getAcceptLinks() {
        return acceptLinks;
    }

    public void setAcceptLinks(Set<ArgumentationLink> acceptLinks) {
        this.acceptLinks = acceptLinks;
    }

    public Set<ArgumentationLink> getRejectLinks() {
        return rejectLinks;
    }

    public void setRejectLinks(Set<ArgumentationLink> rejectLinks) {
        this.rejectLinks = rejectLinks;
    }

    @Override
    public String toString() {
        return "DialogStateHypothesis{" +
                "discourseUnitHypothesisMap=" + discourseUnitHypothesisMap +
                ", acceptLinks=" + acceptLinks +
                ", rejectLinks=" + rejectLinks +
                '}';
    }
}
