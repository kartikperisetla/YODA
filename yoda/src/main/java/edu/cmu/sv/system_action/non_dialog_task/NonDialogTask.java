package edu.cmu.sv.system_action.non_dialog_task;

import edu.cmu.sv.system_action.SystemAction;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 8/27/14.
 */
public abstract class NonDialogTask extends SystemAction {

    public enum TaskStatus {SUCCESSFULLY_COMPLETED, CURRENTLY_EXECUTING_BLOCKING, CURRENTLY_EXECUTING_NOT_BLOCKING, FAILED}
    protected JSONObject taskSpec;

    public void setTaskSpec(JSONObject taskSpec){
        this.taskSpec = taskSpec;
    }

    public JSONObject getTaskSpec(){
        return taskSpec;
    }

    // return preferences object
    public abstract NonDialogTaskPreferences getPreferences();

    // interpret result as the probability that the taskSpec can be executed (must be 0-1)
    public abstract double assessExecutability();

    public void execute(){
        System.out.println("executing non-dialog task:"+this.getClass().getSimpleName()+" "+taskSpec);
    }

    // return the string status indicator for the taskID
    public abstract TaskStatus status(String taskID);

}
