package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/*
 * Created by David Cohen on 9/19/14.
 */
public interface DialogStateUpdateInference {

    /*
    * Return all the new hypotheses generated from applying this inference
    *
    * The returned values won't replace the previous hypotheses, but its hypotheses will
    * be weighted by the assumed hypothesis' prior and collected to create the new tracking state
    * */
    public Pair<Map<String, DialogStateHypothesis>, StringDistribution> applyAll(
            DialogStateHypothesis currentState, Turn turn, long timeStamp);

}
