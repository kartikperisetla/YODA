package edu.cmu.sv.dialog_state_tracking;


import edu.cmu.sv.dialog_management.ActionAnalysis;
import edu.cmu.sv.semantics.SemanticsModel;


/**
 * Created by David Cohen on 9/17/14.
 */
public class DiscourseUnit {
    public SemanticsModel spokenByMe;
    public SemanticsModel spokenByThem;
    public Long timeOfLastActByThem;
    public Long timeOfLastActByMe;
    public String initiator;
    public SemanticsModel groundTruth; // if self-initiated
    public SemanticsModel groundInterpretation; // if other-initiated

    public ActionAnalysis actionAnalysis = new ActionAnalysis();


    public Long getMostRecentContributionTime(){
        Long ans = (long) 0;
        if (timeOfLastActByMe!=null)
            ans = Long.max(timeOfLastActByMe, ans);
        if (timeOfLastActByThem!=null)
            ans = Long.max(timeOfLastActByThem, ans);
        return ans;
    }

    public Object getFromInitiator(String slotPath){
        if (initiator.equals("user")){
            return spokenByThem.newGetSlotPathFiller(slotPath);
        } else { //initiator.equals("system")
            return spokenByMe.newGetSlotPathFiller(slotPath);
        }
    }

    public DiscourseUnit deepCopy(){
        DiscourseUnit ans = new DiscourseUnit();
        if (spokenByMe!=null)
            ans.spokenByMe = spokenByMe.deepCopy();
        if (spokenByThem!=null)
            ans.spokenByThem = spokenByThem.deepCopy();
        ans.timeOfLastActByMe = timeOfLastActByMe;
        ans.timeOfLastActByThem = timeOfLastActByThem;
        ans.initiator = initiator;
        if (groundTruth!=null)
            ans.groundTruth = groundTruth.deepCopy();
        if (groundInterpretation!=null)
            ans.groundInterpretation = groundInterpretation.deepCopy();
        ans.actionAnalysis = actionAnalysis.deepCopy();
        return ans;
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

    public Long getTimeOfLastActByThem() {
        return timeOfLastActByThem;
    }

    public void setTimeOfLastActByThem(Long timeOfLastActByThem) {
        this.timeOfLastActByThem = timeOfLastActByThem;
    }

    public Long getTimeOfLastActByMe() {
        return timeOfLastActByMe;
    }

    public void setTimeOfLastActByMe(Long timeOfLastActByMe) {
        this.timeOfLastActByMe = timeOfLastActByMe;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public SemanticsModel getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(SemanticsModel groundTruth) {
        this.groundTruth = groundTruth;
    }

    public SemanticsModel getGroundInterpretation() {
        return groundInterpretation;
    }

    public void setGroundInterpretation(SemanticsModel groundInterpretation) {
        this.groundInterpretation = groundInterpretation;
    }

    @Override
    public String toString() {
        return "DiscourseUnitHypothesis{" +
                "initiator='" + initiator +
                "', timeOfLastActByThem=" + timeOfLastActByThem +
                ", timeOfLastActByMe=" + timeOfLastActByMe +
                "\nspokenByMe=" + spokenByMe +
                "\nspokenByThem=" + spokenByThem +
                "\ngroundTruth=" + groundTruth +
                "\ngroundInterpretation=" + groundInterpretation +
                '}';
    }

}
