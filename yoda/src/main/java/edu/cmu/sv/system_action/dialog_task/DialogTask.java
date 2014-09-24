package edu.cmu.sv.system_action.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestVerbRole;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestVerb;
import edu.cmu.sv.utils.StringDistribution;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/3/14.
 *
 * Provide functions for accessing and modifying databases to perform standard dialog tasks.
 * Define the penalties and rewards for dialog tasks.
 *
 */
public abstract class DialogTask implements SystemAction {
    protected SemanticsModel taskSpec = null;
    protected Database db;

    // return preferences object
    public abstract DialogTaskPreferences getPreferences();

    public void setTaskSpec(SemanticsModel taskSpec){
        this.taskSpec = taskSpec;
    }

    public SemanticsModel getTaskSpec(){
        return taskSpec;
    }

    // a generic function to see if the important parameters for this task match up
    public boolean meetsTaskSpec(SemanticsModel otherSpec){
        return otherSpec.equals(getTaskSpec());
    }

    /*
    * RequestVerb is a standard slot-filling act that will be used by most dialog tasks
    * when the verb is not included in the DU
    * at least one semantic role is required to generate this dialog act,
    * so we take the most `important' one
    * */
    public DialogAct requestVerb(){
        String verb = taskSpec.getSlots().get("verb");
        String parameterValue = taskSpec.getSlots().get("Agent");
        if (parameterValue==null)
            parameterValue = taskSpec.getSlots().get("Patient");
        if (parameterValue==null)
            parameterValue = taskSpec.getSlots().get("Theme");
        if (parameterValue==null && verb==null)
            return null;
        if (verb==null) {
            Map<String, String> bindings = new HashMap<>();
            bindings.put("v1", parameterValue);
            return new RequestVerb().bindVariables(bindings);
        }
        return null;
    }

    /*
    * requestMissingRequiredVerbRoles
    * generates slot-filling dialog acts for any roles which the DU's verb requires,
    * but aren't present in the DU yet.
    * */
    public Collection<DialogAct> requestMissingRequiredVerbRoles(){
        Collection<DialogAct> ans = new HashSet<>();
        String verb = taskSpec.getSlots().get("verb");
        if (verb==null) {
            return ans;
        }

        Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);
        Set<Class <? extends Role>> requiredRoles = null;
        try {
            requiredRoles = verbClass.newInstance().getRequiredRoles();
            Set<Class<? extends Role>> missingRoles = requiredRoles.stream().
                    filter(x -> !taskSpec.getSlots().containsKey(x.getSimpleName())).
                    collect(Collectors.toSet());

            for (Class<? extends Role> roleCls : missingRoles){
                Map<String, String> bindings = new HashMap<>();
                bindings.put("r1", roleCls.getSimpleName());
                bindings.put("v1", verb);
                ans.add(new RequestVerbRole().bindVariables(bindings));
            }

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ans;
    }

    /*
    * suggestDisambiguatingValues
    * A standard slot-filling dialog task.
    * This looks at some selected roles, and if they are highly ambiguous in the database,
    * this returns some suggested values which might disambiguate the referents
    * */
    public Collection<DialogAct> suggestDisambiguatingValues(){
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
        // TODO: actually implement the part that decides what to suggest
        return new HashSet<>();
    }

    public Map<DialogAct, Double> enumerateAndEvaluateSlotFillingActions(){
        Map<DialogAct, Double> ans = new HashMap<>();
        DialogAct rV = requestVerb();
        if (null!=rV){
            ans.put(rV, RewardAndCostCalculator.rewardForNecessarySlotFilling);
            return ans;
        }
        Collection<DialogAct> missingVerbRoleRequests = requestMissingRequiredVerbRoles();
        missingVerbRoleRequests.stream().
                forEach(x -> ans.put(x, RewardAndCostCalculator.rewardForNecessarySlotFilling));
        return ans;
    }

    // interpret result as the probability that the taskSpec can be executed (must be 0-1)
    // if it isn't executable, then the task spec is probably 'nonsense', since this is a dialog task
    public double assessExecutability(){
        return 1.0;
    }

    // eventually add other stuff to actually implement basic IR / IE.
    public abstract void execute();

}
