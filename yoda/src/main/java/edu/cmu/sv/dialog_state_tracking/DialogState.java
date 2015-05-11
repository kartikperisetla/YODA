package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences.MisunderstoodTurnInference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 12/1/14.
 */
public class DialogState {
    public Map<String, DiscourseUnit> discourseUnitHypothesisMap = new HashMap<>();
    public Set<ArgumentationLink> argumentationLinks = new HashSet<>();
    public long discourseUnitCounter = 0;
    public int misunderstandingCounter = 0;

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

        @Override
        public String toString() {
            return "ArgLink{" +
                    "predecessor='" + predecessor + '\'' +
                    ", successor='" + successor + '\'' +
                    '}';
        }
    }

    /*
    * clean() eliminates un-linked user DUs if there are other more recent unlinked user DUs
    * */
    public void clean(){
        Set<String> activeDiscourseUnits = new HashSet<>();
        Long mostRecentContributionTime = (long) 0;
        for (String discourseUnitKey : discourseUnitHypothesisMap.keySet()){
            String dialogAct = (String)discourseUnitHypothesisMap.get(discourseUnitKey).getFromInitiator("dialogAct");
            boolean anyLink = false;
            for (ArgumentationLink link : argumentationLinks){
                if (link.getPredecessor().equals(discourseUnitKey) || link.getSuccessor().equals(discourseUnitKey)){
                    anyLink = true;
                    break;
                }
            }
            if (!anyLink) {
                activeDiscourseUnits.add(discourseUnitKey);
                if (!dialogAct.equals(MisunderstoodTurnInference.duString)) {
                    mostRecentContributionTime = Long.max(mostRecentContributionTime,
                            discourseUnitHypothesisMap.get(discourseUnitKey).getMostRecentContributionTime());
                }
            }
        }

        if (activeDiscourseUnits.size()<=1)
            return;

        for (String discourseUnitKey : activeDiscourseUnits){
            if (discourseUnitHypothesisMap.get(discourseUnitKey).getMostRecentContributionTime() < mostRecentContributionTime) {
                discourseUnitHypothesisMap.remove(discourseUnitKey);
//                System.err.println("DialogState.clean(): removing an old active discourse unit");
            }
        }
    }

    public DiscourseUnit activeDiscourseUnit(){
        DiscourseUnit ans = null;
        Long mostRecentContributionTime = (long) 0;
        for (String discourseUnitKey : discourseUnitHypothesisMap.keySet()) {
            long thisDuTime = discourseUnitHypothesisMap.get(discourseUnitKey).getMostRecentContributionTime();
            if (mostRecentContributionTime < thisDuTime){
                ans = discourseUnitHypothesisMap.get(discourseUnitKey);
                mostRecentContributionTime = thisDuTime;
            }
        }

        return ans;
    }


    public DialogState deepCopy(){
        DialogState ans = new DialogState();
        for (ArgumentationLink link : argumentationLinks)
            ans.argumentationLinks.add(new ArgumentationLink(link.predecessor, link.successor));
        for (String key : discourseUnitHypothesisMap.keySet()){
            ans.discourseUnitHypothesisMap.put(key, discourseUnitHypothesisMap.get(key).deepCopy());
        }
        ans.discourseUnitCounter = discourseUnitCounter;
        ans.misunderstandingCounter = misunderstandingCounter;
        return ans;
    }

    public Map<String, DiscourseUnit> getDiscourseUnitHypothesisMap() {
        return discourseUnitHypothesisMap;
    }

    public void setDiscourseUnitHypothesisMap(Map<String, DiscourseUnit> discourseUnitHypothesisMap) {
        this.discourseUnitHypothesisMap = discourseUnitHypothesisMap;
    }

    public Set<ArgumentationLink> getArgumentationLinks() {
        return argumentationLinks;
    }

    public void setArgumentationLinks(Set<ArgumentationLink> argumentationLinks) {
        this.argumentationLinks = argumentationLinks;
    }

    @Override
    public String toString() {
        String ans = "DialogStateHypothesis{" +
                "discourseUnitHypothesisMap=\n";
        for (String key : discourseUnitHypothesisMap.keySet()){
            ans += key+"\n";
            ans += discourseUnitHypothesisMap.get(key)+"\n";
        }
        ans +="argumentationLinks=" + argumentationLinks +
                '}';
        return ans;
    }
}
