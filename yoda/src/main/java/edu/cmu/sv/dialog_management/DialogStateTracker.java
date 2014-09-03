package edu.cmu.sv.dialog_management;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/2/14.
 *
 * The DialogStateTracker class keeps track of a set of Discourse Units
 * and makes decisions about how to update them upon receiving new input
 *
 */
public class DialogStateTracker {
    Set<DiscourseUnit> discourseUnits;

    public Set<DiscourseUnit> getDiscourseUnits() {
        return discourseUnits;
    }

    public void updateDialogState(Map<String, SemanticsModel> utterances, StringDistribution weights, Float timeStamp){}

}
