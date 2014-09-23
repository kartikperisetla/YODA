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
import java.util.stream.Collectors;

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
        String action = taskSpec.getSlots().get("verb");
        if (action==null){
            System.out.println("there is no verb, so I should ask for one");
            //todo: return a "what about X?" utterance
        }

        Class<? extends Verb> actionClass = OntologyRegistry.verbNameMap.get(action);
        Set<Class <? extends Role>> requiredRoles = null;
        try {
            requiredRoles = actionClass.newInstance().getRequiredRoles();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Set<Class <? extends Role>> missingRoles = requiredRoles.stream().
                filter(x -> !taskSpec.getSlots().containsKey(x.getSimpleName())).
                collect(Collectors.toSet());

        if (!missingRoles.isEmpty()){
            //todo: request a role that's currently missing
            System.out.println("at this point I should request a role which is missing");
        }

        // query the entire statement
        // don't make use of the descriptions / bindings which have already been collected
        // todo: give a response
        System.out.println("query result:"+sparqlTools.ynQuestionResult(db, taskSpec));


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
