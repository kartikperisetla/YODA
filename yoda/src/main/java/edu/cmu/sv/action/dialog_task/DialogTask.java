package edu.cmu.sv.action.dialog_task;

import edu.cmu.sv.action.Action;
import edu.cmu.sv.semantics.SemanticsModel;

/**
 * Created by David Cohen on 9/3/14.
 *
 * Provide functions for accessing and modifying databases to perform standard dialog tasks.
 * Define the penalties and rewards for dialog tasks.
 *
 */
public interface DialogTask extends Action{
    // return preferences object
    public DialogTaskPreferences getPreferences();

    // accessors for the DialogTask's taskSpec
    public void setTaskSpec(SemanticsModel taskSpec);
    public SemanticsModel getTaskSpec();
    // a generic function to see if the important parameters for this task match up
    public default boolean meetsTaskSpec(SemanticsModel otherSpec){
        return otherSpec.equals(getTaskSpec());
    };

    // interpret result as the probability that the taskSpec can be executed (must be 0-1)
    // if it isn't executable, then the task spec is probably 'nonsense', since this is a dialog task
    public default double assessExecutability(){
        return 1.0;
    }

    // eventually add other stuff to actually implement basic IR / IE.
    public void execute();

}
