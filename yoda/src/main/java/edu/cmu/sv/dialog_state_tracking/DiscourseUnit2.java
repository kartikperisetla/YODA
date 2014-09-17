package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cohend on 9/17/14.
 */
public class DiscourseUnit2 {

    StringDistribution hypothesisDistribution;
    Map<String, SemanticsModel> spokenByThem;
    Map<String, SemanticsModel> understoodByThem;
    SemanticsModel spokenByMe;
    float timeOfLastThemAct;
    float timeOfLastActByMe;

    public void updateDiscourseUnit(Map<String, SemanticsModel> utteranceHypotheses,
                                    StringDistribution weights,
                                    String speaker,
                                    float timeOfAct){}

    public StringDistribution getHypothesisDistribution() {
        return hypothesisDistribution;
    }

    public void setHypothesisDistribution(StringDistribution hypothesisDistribution) {
        this.hypothesisDistribution = hypothesisDistribution;
    }

    public Map<String, SemanticsModel> getSpokenByThem() {
        return spokenByThem;
    }

    public void setSpokenByThem(Map<String, SemanticsModel> spokenByThem) {
        this.spokenByThem = spokenByThem;
    }

    public Map<String, SemanticsModel> getUnderstoodByThem() {
        return understoodByThem;
    }

    public void setUnderstoodByThem(Map<String, SemanticsModel> understoodByThem) {
        this.understoodByThem = understoodByThem;
    }

    public SemanticsModel getSpokenByMe() {
        return spokenByMe;
    }

    public void setSpokenByMe(SemanticsModel spokenByMe) {
        this.spokenByMe = spokenByMe;
    }

    public float getTimeOfLastThemAct() {
        return timeOfLastThemAct;
    }

    public void setTimeOfLastThemAct(float timeOfLastThemAct) {
        this.timeOfLastThemAct = timeOfLastThemAct;
    }

    public float getTimeOfLastActByMe() {
        return timeOfLastActByMe;
    }

    public void setTimeOfLastActByMe(float timeOfLastActByMe) {
        this.timeOfLastActByMe = timeOfLastActByMe;
    }


}
