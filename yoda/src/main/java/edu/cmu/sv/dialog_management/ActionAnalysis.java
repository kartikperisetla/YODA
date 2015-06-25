package edu.cmu.sv.dialog_management;

import com.google.common.collect.Iterables;
import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.database.ReferenceResolution;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.domain.ontology.Role;
import edu.cmu.sv.domain.ontology.Verb;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.utils.StringDistribution;
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
    public Map<String, Object> responseStatement = new HashMap<>();
    public Set<String> missingRequiredVerbSlots = new HashSet<>();
    public Set<NonDialogTask> enumeratedNonDialogTasks = new HashSet<>();

    public ActionAnalysis deepCopy(){
        ActionAnalysis ans = new ActionAnalysis();
        ans.missingRequiredVerbSlots = new HashSet<>(missingRequiredVerbSlots);
        ans.enumeratedNonDialogTasks = new HashSet<>(enumeratedNonDialogTasks);
        ans.responseStatement = new HashMap<>();
        for (String key : responseStatement.keySet()){
            if (responseStatement.get(key) instanceof JSONObject)
                ans.responseStatement.put(key, SemanticsModel.parseJSON(((JSONObject)responseStatement.get(key)).toJSONString()));
            ans.responseStatement.put(key, responseStatement.get(key));
        }
        return ans;
    }

    public void update(YodaEnvironment yodaEnvironment, DiscourseUnit discourseUnit) {
        missingRequiredVerbSlots.clear();
        enumeratedNonDialogTasks.clear();
        String dialogActString = (String) discourseUnit.getSpokenByThem().newGetSlotPathFiller("dialogAct");

        SemanticsModel groundedMeaning = discourseUnit.getGroundInterpretation();
        String verb = (String) groundedMeaning.newGetSlotPathFiller("verb.class");
        Verb verbClass = Ontology.verbNameMap.get(verb);

        for (Role requiredRole : Iterables.concat(verbClass.getRequiredDescriptions(), verbClass.getRequiredGroundedRoles())) {
            if (groundedMeaning.newGetSlotPathFiller("verb." + requiredRole.name) == null) {
                missingRequiredVerbSlots.add("verb." + requiredRole.name);
            }
        }

        responseStatement = new HashMap<>();

        if (missingRequiredVerbSlots.size()==0) {
            if (dialogActString.equals(YNQuestion.class.getSimpleName()) || dialogActString.equals(WHQuestion.class.getSimpleName())){
                if (verbClass.equals(YodaSkeletonOntologyRegistry.hasProperty)) {
                    JSONObject agent = (JSONObject)(groundedMeaning.newGetSlotPathFiller("verb.Agent"));
                    JSONObject patient = (JSONObject)(groundedMeaning.newGetSlotPathFiller("verb.Patient"));
                    Double match = ReferenceResolution.descriptionMatch(yodaEnvironment, agent, patient);
//                    System.err.println("ActionAnalysis: grounded meaning:"+groundedMeaning);
//                    System.err.println("ActionAnalysis: Description match:"+match);

                    if (match==null){
                        responseStatement.put("dialogAct", DontKnow.class.getSimpleName());
                    } else {
                        if (match > .5) {
                            responseStatement.put("dialogAct", Statement.class.getSimpleName());
                            responseStatement.put("verb.Agent", SemanticsModel.parseJSON(agent.toJSONString()));
                            JSONObject responseDescription = SemanticsModel.parseJSON(patient.toJSONString());
                            responseDescription.put("refType", "indefinite");
                            responseStatement.put("verb.Patient", SemanticsModel.parseJSON(responseDescription.toJSONString()));
                        } else {
                            responseStatement.put("dialogAct", Reject.class.getSimpleName());
                        }
                    }

                } else if (verbClass.equals(YodaSkeletonOntologyRegistry.exist)){
                    JSONObject searchDescription = (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Agent");
//                    System.err.println("ActionAnalysis.grounded meaning:" + groundedMeaning);
                    StringDistribution recommendations = ReferenceResolution.resolveReference(yodaEnvironment, searchDescription, false, true);
//                    System.err.println("recommendations:" + recommendations);
                    String bestRecommendation = recommendations.getTopHypothesis();
                    JSONObject responseDescription = SemanticsModel.parseJSON(searchDescription.toJSONString());
                    if (bestRecommendation==null){
                        responseDescription.put("refType", "indefinite");
                        responseStatement.put("dialogAct", SearchReturnedNothing.class.getSimpleName());
                        responseStatement.put("verb.Patient", responseDescription);
                    } else {
                        responseDescription.put("refType", "indefinite");
                        responseStatement.put("dialogAct", Statement.class.getSimpleName());
                        responseStatement.put("verb.Agent", SemanticsModel.parseJSON(Ontology.webResourceWrap(bestRecommendation)));
                        responseStatement.put("verb.Patient", responseDescription);
//                    System.out.println("ActionAnalysis: response statement:\n" + responseStatement);
                    }
                }
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
