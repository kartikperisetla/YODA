package edu.cmu.sv.dialog_state_tracking;

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
    }

}
