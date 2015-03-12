package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.Map;

/*
 * Created by David Cohen on 9/19/14.
 * Turn contains an SLU result / SLU ground truth for a single user/system turn
 */
public class Turn {
    Map<String, SemanticsModel> hypotheses;
    SemanticsModel systemUtterance;
    SemanticsModel groundedSystemMeaning;
    StringDistribution hypothesisDistribution;
    String speaker;

    public Turn(String speaker, SemanticsModel systemUtterance, SemanticsModel groundedSystemMeaning, Map<String, SemanticsModel> hypotheses, StringDistribution hypothesisDistribution) {
        this.hypotheses = hypotheses;
        this.systemUtterance = systemUtterance;
        this.groundedSystemMeaning = groundedSystemMeaning;
        this.hypothesisDistribution = hypothesisDistribution;
        this.speaker = speaker;
    }

    public Map<String, SemanticsModel> getHypotheses() {
        return hypotheses;
    }

    public StringDistribution getHypothesisDistribution() {
        return hypothesisDistribution;
    }

    public String getSpeaker() {
        return speaker;
    }

    public SemanticsModel getSystemUtterance() {
        return systemUtterance;
    }

    public SemanticsModel getGroundedSystemMeaning() {
        return groundedSystemMeaning;
    }

    @Override
    public String toString() {
        return "Turn{" +
                "hypotheses=" + hypotheses +
                ", systemUtterance=" + systemUtterance +
                ", groundedSystemMeaning=" + groundedSystemMeaning +
                ", hypothesisDistribution=" + hypothesisDistribution +
                ", speaker='" + speaker + '\'' +
                '}';
    }
}
