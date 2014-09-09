package edu.cmu.sv.task_interface.non_dialog_task;

import edu.cmu.sv.semantics.SemanticsModel;

/**
 * Created by David Cohen on 8/27/14.
 */
public interface NonDialogTask {

    public enum TaskStatus {SUCCESSFULLY_COMPLETED, CURRENTLY_EXECUTING_BLOCKING, CURRENTLY_EXECUTING_NOT_BLOCKING, FAILED}

    // return preferences object
    public NonDialogTaskPreferences getPreferences();

    // accessors for the DialogTask's taskSpec
    public void setTaskSpec(SemanticsModel taskSpec);
    public SemanticsModel getTaskSpec();
    // a generic function to see if the important parameters for this task match up
    public boolean meetsTaskSpec(SemanticsModel otherSpec);

    // interpret result as the probability that the taskSpec can be executed (must be 0-1)
    public double assessExecutability();

    // return the string identifier of the executing task (taskID)
    public String execute(SemanticsModel taskSpec);

    // return the string status indicator for the taskID
    public TaskStatus status(String taskID);

}
