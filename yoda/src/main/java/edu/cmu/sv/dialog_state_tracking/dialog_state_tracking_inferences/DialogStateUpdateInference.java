package edu.cmu.sv.dialog_state_tracking.dialog_state_tracking_inferences;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

/*
 * Created by David Cohen on 9/19/14.
 */
public abstract class DialogStateUpdateInference {

    /*
    * Return all the new hypotheses generated from applying this inference
    *
    * The returned values won't replace the previous hypotheses, but its hypotheses will
    * be weighted by the assumed hypothesis' prior and collected to create the new tracking state
    * */
    public abstract NBestDistribution<DialogState> applyAll(
            YodaEnvironment yodaEnvironment, DialogState currentState, Turn turn, long timeStamp);

}
