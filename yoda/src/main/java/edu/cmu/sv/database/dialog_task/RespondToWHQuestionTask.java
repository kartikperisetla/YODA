package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.database.Database;

/**
 * Created by David Cohen on 9/3/14.
 */
public class RespondToWHQuestionTask extends DialogTask {
    private static DialogTaskPreferences preferences = new DialogTaskPreferences(.5,1,2);

    public RespondToWHQuestionTask(Database db) {
        this.db = db;
    }

    @Override
    public void execute() {
        System.out.println("executing 'RespondToWHQuestionTask'");
    }

    @Override
    public DialogTaskPreferences getPreferences() {
        return preferences;
    }

}
