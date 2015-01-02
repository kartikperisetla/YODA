package edu.cmu.sv.system_action;

/**
 * Created by David Cohen on 12/21/14.
 *
 * Executor object is used to manage the execution of system actions.
 * Sends feedback to dialog state tracker.
 *
 */
public interface Executor {
    public void execute(SystemAction systemAction);
}
