package edu.cmu.sv.dialog_management;

import com.google.common.collect.Iterables;
import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.database.ReferenceResolution;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.domain.ontology2.Quality2;
import edu.cmu.sv.domain.ontology2.QualityDegree;
import edu.cmu.sv.domain.ontology2.Role2;
import edu.cmu.sv.domain.ontology2.Verb2;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
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
        Verb2 verbClass = Ontology.verbNameMap.get(verb);

        for (Role2 requiredRole : Iterables.concat(verbClass.getRequiredDescriptions(), verbClass.getRequiredGroundedRoles())) {
            if (groundedMeaning.newGetSlotPathFiller("verb." + requiredRole.name) == null) {
                missingRequiredVerbSlots.add("verb." + requiredRole.name);
            }
        }

        responseStatement = new HashMap<>();

        if (missingRequiredVerbSlots.size()==0) {
            if (dialogActString.equals(YNQuestion.class.getSimpleName()) || dialogActString.equals(WHQuestion.class.getSimpleName())){
                if (verbClass.equals(YodaSkeletonOntologyRegistry.hasProperty)) {
                    String entityURI = (String) groundedMeaning.newGetSlotPathFiller("verb.Agent.HasURI");
                    Quality2 requestedQualityClass;
                    if (dialogActString.equals(WHQuestion.class.getSimpleName())) {
                        requestedQualityClass = Ontology.qualityNameMap.get(
                                        (String) groundedMeaning.newGetSlotPathFiller("verb.Patient.HasValue.class"));
                    } else {
                        Set<Object> patientRoles = ((JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Patient")).keySet();
                        Role2 suggestedRole = null;
                        for (Object role : patientRoles) {
                            if (Ontology.roleNameMap.containsKey(role) && Ontology.roleNameMap.get(role).isQualityRole) {
                                suggestedRole = Ontology.roleNameMap.get(role);
                                break;
                            }
                        }
                        if (suggestedRole == null) {
                            throw new Error("no role has been suggested");
                        }
                        requestedQualityClass = Ontology.qualityNameMap.get(suggestedRole.name.substring(3));
                    }

                    // todo: replace with new quality argument accessor interface
                    Object firstQualityArgument = requestedQualityClass.firstArgumentClassConstraint;
                    Object secondQualityArgument = requestedQualityClass.secondArgumentClassConstraint;
                    if (secondQualityArgument != null)
                        throw new Error("the requested quality isn't an adjective");

                    boolean dontKnow = false;
                    StringDistribution adjectiveScores = new StringDistribution();
                    Pair<Role2, Set<QualityDegree>> descriptor = Ontology.qualityDescriptors(requestedQualityClass);
                    for (QualityDegree adjectiveClass : descriptor.getRight()) {
                        Double degreeOfMatch = yodaEnvironment.db.evaluateQualityDegree(entityURI, null, adjectiveClass);
                        if (degreeOfMatch == null) {
                            dontKnow = true;
                            responseStatement.put("dialogAct", DontKnow.class.getSimpleName());
                        } else {
                            adjectiveScores.put(adjectiveClass.name, degreeOfMatch);
                        }
                    }

                    if (!dontKnow) {
                        QualityDegree adjectiveClass = Ontology.qualityDegreeNameMap.get(adjectiveScores.getTopHypothesis());
                        if (adjectiveClass == null) {
                            responseStatement.put("dialogAct", DontKnow.class.getSimpleName());
                        } else {
                            JSONObject description = SemanticsModel.parseJSON("{\"class\":\"" + adjectiveClass.name + "\"}");
                            SemanticsModel.wrap(description, YodaSkeletonOntologyRegistry.unknownThingWithRoles.name,
                                    descriptor.getLeft().name);
                            responseStatement.put("dialogAct", Statement.class.getSimpleName());
                            responseStatement.put("verb.Agent", SemanticsModel.parseJSON(Ontology.webResourceWrap(entityURI)));
                            responseStatement.put("verb.Patient", description);
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
