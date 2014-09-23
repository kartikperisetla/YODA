package edu.cmu.sv.system_action.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.action.Verb;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/3/14.
 *
 * This task answers a yes/no question by performing appropriate database lookups
 */
public class RespondToYNQuestionTask implements DialogTask {
    private static DialogTaskPreferences preferences = new DialogTaskPreferences(.5,1,2);
    private SemanticsModel taskSpec = null;

    private Database db;

    public RespondToYNQuestionTask(Database db) {
        this.db = db;
    }

    @Override
    public void execute() {
        System.out.println("executing 'RespondToYNQuestionTask'");

        // YN questions ask about the relationships between entities or between entities and descriptions
        Map<String, StringDistribution> bindings = new HashMap<>();
        Map<String, SemanticsModel> descriptions = new HashMap<>();

        for (String slot : taskSpec.getSlots().keySet()){
            if (slot.equals("dialogAct"))
                continue;
            if (taskSpec.getChildren().containsKey(taskSpec.getSlots().get(slot))) {
                SemanticsModel description = taskSpec.getChildren().get(taskSpec.getSlots().get(slot));
                bindings.put(slot, sparqlTools.possibleReferents(db, description));
                descriptions.put(slot, description);
            }
        }

        //// check for query validity
        String action = taskSpec.getSlots().get("action");
        if (action==null){
            //todo: return a "what about X?" utterance
        }

        Class<? extends Verb> actionClass = OntologyRegistry.verbNameMap.get(action);
        Set<Class <? extends Role>> requiredRoles = null;
        try {
            requiredRoles = actionClass.newInstance().getRequiredRoles();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if ()

        // query the entire statement



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

}
