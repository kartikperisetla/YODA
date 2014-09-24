package edu.cmu.sv.system_action.dialog_task;

import edu.cmu.sv.database.Database;

/**
 * Created by David Cohen on 9/3/14.
 *
 * This task answers a yes/no question by performing appropriate database lookups
 */
public class RespondToYNQuestionTask extends DialogTask {
    private static DialogTaskPreferences preferences = new DialogTaskPreferences(.5,1,2);

    public RespondToYNQuestionTask(Database db) {
        this.db = db;
    }

    @Override
    public void execute() {
        System.out.println("executing 'RespondToYNQuestionTask'");

        // query the entire statement
        // don't make use of the descriptions / bindings which have already been collected
        // todo: generate a dialog act
        System.out.println("query result:"+sparqlTools.ynQuestionResult(db, taskSpec));
    }

    @Override
    public double assessExecutability() {
        if (requestVerb()!=null || requestMissingRequiredVerbRoles().size()>0)
            return 0.0;
        return 1.0;
    }

    @Override
    public DialogTaskPreferences getPreferences() {
        return preferences;
    }

}
