package edu.cmu.sv.action.dialog_task;

import edu.cmu.sv.semantics.SemanticsModel;

/**
 * Created by David Cohen on 9/3/14.
 */
public class RespondToWHQuestionTask implements DialogTask {
    private static DialogTaskPreferences preferences = new DialogTaskPreferences(1,5,3);
    private SemanticsModel taskSpec = null;

    @Override
    public void execute() {
        System.out.println("executing 'RespondToWHQuestionTask'");
    }

    @Override
    public DialogTaskPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void setTaskSpec(SemanticsModel taskSpec) {
        this.taskSpec = taskSpec;
    }

    @Override
    public SemanticsModel getTaskSpec() {
        return taskSpec;
    }

    @Override
    public double assessExecutability() {
        return 0;
    }
}
