package edu.cmu.sv.system_action.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public DialogTaskPreferences getPreferences() {
        return preferences;
    }

}
