package edu.cmu.sv.system_action.non_dialog_task;

import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.semantics.SemanticsModel;

/**
 * Created by David Cohen on 8/27/14.
 */
public abstract class NonDialogTask extends SystemAction {

    public enum TaskStatus {SUCCESSFULLY_COMPLETED, CURRENTLY_EXECUTING_BLOCKING, CURRENTLY_EXECUTING_NOT_BLOCKING, FAILED}
    protected SemanticsModel taskSpec;

    public void setTaskSpec(SemanticsModel taskSpec){
        this.taskSpec = taskSpec;
    }

    public SemanticsModel getTaskSpec(){
        return taskSpec;
    }

    // return preferences object
    public abstract NonDialogTaskPreferences getPreferences();

    // TODO: implement a default that actually works
    // a generic function to see if the important parameters for this task match up
    public boolean meetsTaskSpec(SemanticsModel otherSpec){
        return otherSpec.equals(getTaskSpec());
    }

    // interpret result as the probability that the taskSpec can be executed (must be 0-1)
    public abstract double assessExecutability();

    // return the string identifier of the executing task (taskID)
    public abstract String execute(SemanticsModel taskSpec);

    // return the string status indicator for the taskID
    public abstract TaskStatus status(String taskID);

}
