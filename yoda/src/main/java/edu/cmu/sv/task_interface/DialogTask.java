package edu.cmu.sv.task_interface;

import edu.cmu.sv.semantics.SemanticsModel;

/**
 * Created by David Cohen on 9/3/14.
 *
 * Provide functions for accessing and modifying databases to perform standard dialog tasks.
 * Define the penalties and rewards for dialog tasks.
 *
 */
public interface DialogTask {
    public enum TaskStatus {SUCCESSFULLY_COMPLETED, CURRENTLY_EXECUTING_BLOCKING, CURRENTLY_EXECUTING_NOT_BLOCKING, FAILED}

    // return preferences object
    public DialogTaskPreferences getPreferences();

    // eventually add other stuff to actually implement basic IR / IE.

}
