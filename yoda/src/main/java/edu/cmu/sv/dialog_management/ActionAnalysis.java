package edu.cmu.sv.dialog_management;

import com.google.common.collect.Iterables;
import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
* Created by David Cohen on 12/17/14.
*/ /*
* Performs and contains results for analysis of the action-related consequences of a discourse unit
* */
public class ActionAnalysis {
    public Double ynqTruth;
    public Set<String> missingRequiredVerbSlots = new HashSet<>();

    public ActionAnalysis deepCopy(){
        ActionAnalysis ans = new ActionAnalysis();
        ans.ynqTruth = ynqTruth;
        ans.missingRequiredVerbSlots = new HashSet<>(missingRequiredVerbSlots);
        return ans;
    }

    public void update(YodaEnvironment yodaEnvironment, DiscourseUnit discourseUnit) {
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
            return;
        }

        if (dialogActString.equals(YNQuestion.class.getSimpleName())) {
            if (verbClass.equals(HasProperty.class)) {
                ynqTruth = ReferenceResolution.descriptionMatch(yodaEnvironment,
                        (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Agent"),
                        (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Patient"));
            }
        }
    }

}
