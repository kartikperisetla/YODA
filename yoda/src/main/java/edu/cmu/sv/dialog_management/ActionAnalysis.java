package edu.cmu.sv.dialog_management;

import com.google.common.collect.Iterables;
import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.WHQuestion;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
* Created by David Cohen on 12/17/14.
*/ /*
* Performs and contains results for analysis of the action-related consequences of a discourse unit
* */
public class ActionAnalysis {
    public Double ynqTruth;
    public Map<String, Object> responseStatement = new HashMap<>();
    public Set<String> missingRequiredVerbSlots = new HashSet<>();
    public Set<NonDialogTask> enumeratedNonDialogTasks = new HashSet<>();

    public ActionAnalysis deepCopy(){
        ActionAnalysis ans = new ActionAnalysis();
        ans.ynqTruth = ynqTruth;
        ans.missingRequiredVerbSlots = new HashSet<>(missingRequiredVerbSlots);
        ans.enumeratedNonDialogTasks = new HashSet<>(enumeratedNonDialogTasks);
        return ans;
    }

    public void update(YodaEnvironment yodaEnvironment, DiscourseUnit discourseUnit) {
        missingRequiredVerbSlots.clear();
        enumeratedNonDialogTasks.clear();
        String dialogActString = (String) discourseUnit.getSpokenByThem().newGetSlotPathFiller("dialogAct");

        SemanticsModel groundedMeaning = discourseUnit.getGroundInterpretation();
        String verb = (String) groundedMeaning.newGetSlotPathFiller("verb.class");
        Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);

        try {
            Verb verbInstance = verbClass.newInstance();
            for (Class<? extends Role> requiredRole : Iterables.concat(verbInstance.getRequiredDescriptions(), verbInstance.getRequiredGroundedRoles())) {
                if (groundedMeaning.newGetSlotPathFiller("verb." + requiredRole.getSimpleName()) == null) {
                    missingRequiredVerbSlots.add("verb." + requiredRole.getSimpleName());
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }

        if (missingRequiredVerbSlots.size()>0) {
            ynqTruth = null;
        } else if (dialogActString.equals(YNQuestion.class.getSimpleName())) {
            if (verbClass.equals(HasProperty.class)) {
                ynqTruth = ReferenceResolution.descriptionMatch(yodaEnvironment,
                        (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Agent"),
                        (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Patient"));
            }
        }

        if (missingRequiredVerbSlots.size()>0) {
            responseStatement = new HashMap<>();
        } else if (dialogActString.equals(WHQuestion.class.getSimpleName())) {
            if (verbClass.equals(HasProperty.class)) {
                responseStatement.put("verb.Agent", groundedMeaning.newGetSlotPathFiller("verb.Agent"));
                Class<? extends Thing> requestedQuality = OntologyRegistry.thingNameMap.get(
                        (String) groundedMeaning.newGetSlotPathFiller("verb.Patient.HasValue.class"));
//                groundedMeaning.findAllPathsToClass()


                responseStatement.put("verb.Patient", groundedMeaning.newGetSlotPathFiller("verb.Agent"));


                ynqTruth = ReferenceResolution.descriptionMatch(yodaEnvironment,
                        (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Agent"),
                        (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Patient"));
            }
        }


        SemanticsModel resolvedMeaning = discourseUnit.getGroundInterpretation();
        if (resolvedMeaning!=null) {
            for (ActionSchema actionSchema : DialogRegistry.actionSchemata) {
                if (actionSchema.matchSchema(resolvedMeaning)) {
                    NonDialogTask enumeratedTask = actionSchema.applySchema(resolvedMeaning);
                    boolean alreadyThere = false;
                    for (NonDialogTask task : enumeratedNonDialogTasks){
                        if (enumeratedTask.evaluationMatch(task)) {
                            alreadyThere = true;
                            break;
                        }
                    }
                    if (!alreadyThere){
                        enumeratedNonDialogTasks.add(enumeratedTask);
                    }
                }
            }
        }


    }

}
