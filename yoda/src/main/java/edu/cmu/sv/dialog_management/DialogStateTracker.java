package edu.cmu.sv.dialog_management;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashSet;
import java.util.LinkedList;
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

    public DialogStateTracker() {
        discourseUnits = new HashSet<>();
    }

    public Set<DiscourseUnit> getDiscourseUnits() {
        return discourseUnits;
    }



    // TODO: implement real dialog state tracking
    public void updateDialogState(Map<String, SemanticsModel> utterances,
                                  StringDistribution weights, Float timeStamp){
        //For now, we assume that there is only one discourse unit,
        //and every utterance is added to it.
        if (discourseUnits.isEmpty())
            discourseUnits.add(new DiscourseUnit());
        DiscourseUnit DUOfInterest = new LinkedList<>(discourseUnits).get(0);
        DUOfInterest.updateDiscourseUnit(utterances, weights, timeStamp);
    }

}
