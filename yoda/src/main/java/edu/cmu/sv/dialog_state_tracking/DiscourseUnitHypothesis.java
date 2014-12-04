package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.dialog_task.DialogTask;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/17/14.
 */
public class DiscourseUnitHypothesis {
    SemanticsModel spokenByMe;
    SemanticsModel spokenByThem;
    Long timeOfLastActByThem;
    Long timeOfLastActByMe;
    String initiator;
    SemanticsModel groundTruth; // if self-initiated
    SemanticsModel groundInterpretation; // if other-initiated

    // analysis for argumentative purposes
    Double ynqTruth;
    Double whqTruth;

    public Long getMostRecentContributionTime(){
        Long ans = (long) 0;
        if (timeOfLastActByMe!=null)
            ans = Long.max(timeOfLastActByMe, ans);
        if (timeOfLastActByThem!=null)
            ans = Long.max(timeOfLastActByThem, ans);
        return ans;
    }

    public Pair<Map<String, DiscourseUnitHypothesis>, StringDistribution> groundAndAnalyse(YodaEnvironment yodaEnvironment){
        try {
            String dialogActString = (String) spokenByThem.newGetSlotPathFiller("dialogAct");
            Class<? extends DialogTask> taskClass = DialogRegistry.dialogTaskMap.
                    get(DialogRegistry.dialogActNameMap.get(dialogActString));
            return taskClass.newInstance().groundAndAnalyse(this, yodaEnvironment);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public DiscourseUnitHypothesis deepCopy(){
        DiscourseUnitHypothesis ans = new DiscourseUnitHypothesis();
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
        ans.ynqTruth = ynqTruth;
        ans.whqTruth = whqTruth;
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

    public Double getYnqTruth() {
        return ynqTruth;
    }

    public void setYnqTruth(Double ynqTruth) {
        this.ynqTruth = ynqTruth;
    }

    public Double getWhqTruth() {
        return whqTruth;
    }

    public void setWhqTruth(Double whqTruth) {
        this.whqTruth = whqTruth;
    }

    @Override
    public String toString() {
        return "DiscourseUnitHypothesis{" +
                "initiator='" + initiator +
                ", timeOfLastActByThem=" + timeOfLastActByThem +
                ", timeOfLastActByMe=" + timeOfLastActByMe +
                "\nynqTruth=" + ynqTruth +
                ", whqTruth=" + whqTruth +
                "\nspokenByMe=" + spokenByMe +
                "\nspokenByThem=" + spokenByThem +
                "\ngroundTruth=" + groundTruth +
                "\ngroundInterpretation=" + groundInterpretation +
                '}';
    }
}
