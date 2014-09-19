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
    StringDistribution hypothesisDistribution;
    String speaker;

    public Turn(String speaker, SemanticsModel systemUtterance, Map<String, SemanticsModel> hypotheses, StringDistribution hypothesisDistribution) {
        this.hypotheses = hypotheses;
        this.systemUtterance = systemUtterance;
        this.hypothesisDistribution = hypothesisDistribution;
        this.speaker = speaker;
    }
}
