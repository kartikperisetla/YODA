package edu.cmu.sv.system_action.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestVerbRole;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestVerb;
import edu.cmu.sv.utils.StringDistribution;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/3/14.
 *
 * Provide functions for accessing and modifying databases to perform standard dialog tasks.
 * Define the penalties and rewards for dialog tasks.
 *
 */
public abstract class DialogTask extends SystemAction {
    protected SemanticsModel taskSpec;

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
    * when the verb class is not defined in the DU
    * */
    public DialogAct requestVerb(){
        String verbClass = (String) taskSpec.newGetSlotPathFiller("verb.class");
        String parameterValue = (String) taskSpec.newGetSlotPathFiller("verb.Agent.class");
        if (parameterValue==null)
            parameterValue = (String) taskSpec.newGetSlotPathFiller("verb.Patient.class");
        if (parameterValue==null)
            parameterValue = (String) taskSpec.newGetSlotPathFiller("verb.Theme.class");
        if (parameterValue==null)
            return null;
        if (verbClass.equals(UnknownThingWithRoles.class.getSimpleName())) {
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("v1", parameterValue);
            DialogAct ans = new RequestVerb();
            ans.bindVariables(bindings);
            return ans;
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
        String verbClassString = (String) taskSpec.newGetSlotPathFiller("verb.class");
        if (verbClassString.equals(UnknownThingWithRoles.class.getSimpleName())) {
            return ans;
        }
        Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verbClassString);
        Set<Class <? extends Role>> requiredRoles = null;
        try {
            requiredRoles = verbClass.newInstance().getRequiredRoles();
            Set<Class<? extends Role>> missingRoles = new HashSet<>();
            for (Class<? extends Role> cls : requiredRoles){
                if (!((JSONObject) taskSpec.newGetSlotPathFiller("verb")).containsKey(cls.getSimpleName()))
                    missingRoles.add(cls);
            }

            for (Class<? extends Role> roleCls : missingRoles){
                Map<String, Object> bindings = new HashMap<>();
                bindings.put("r1", roleCls.getSimpleName());
                bindings.put("v1", verbClassString);
                DialogAct tmp = new RequestVerbRole();
                tmp.bindVariables(bindings);
                ans.add(tmp);
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
    // TODO: implement this
    public Collection<DialogAct> suggestDisambiguatingValues(){
        Map<String, StringDistribution> bindings = new HashMap<>();
        Map<String, SemanticsModel> descriptions = new HashMap<>();

//        for (String slot : taskSpec.getSlots().keySet()){
//            if (slot.equals("dialogAct"))
//                continue;
//            if (taskSpec.getChildren().containsKey(taskSpec.getSlots().get(slot))) {
//                SemanticsModel description = taskSpec.getChildren().get(taskSpec.getSlots().get(slot));
//                bindings.put(slot, sparqlTools.possibleReferents(db, description));
//                descriptions.put(slot, description);
//            }
//        }
        return new HashSet<>();
    }

    public Collection<DialogAct> enumerateAndEvaluateSlotFillingActions(){
        Collection<DialogAct> ans = new HashSet<>();
        DialogAct rV = requestVerb();
        if (null!=rV){
            ans.add(rV);
            return ans;
        }
        ans.addAll(requestMissingRequiredVerbRoles());
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
