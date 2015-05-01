package edu.cmu.sv.dialog_management;

import com.google.common.collect.Iterables;
import edu.cmu.sv.database.ReferenceResolution;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.has_quality_subroles.HasQualityRole;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Exist;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.ActionSchema;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.*;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;

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
        Class<? extends Verb> verbClass = Ontology.verbNameMap.get(verb);

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

        responseStatement = new HashMap<>();

        if (missingRequiredVerbSlots.size()==0) {
            if (dialogActString.equals(YNQuestion.class.getSimpleName()) || dialogActString.equals(WHQuestion.class.getSimpleName())){
                if (verbClass.equals(HasProperty.class)) {
                    String entityURI = (String) groundedMeaning.newGetSlotPathFiller("verb.Agent.HasURI");
                    Class<? extends TransientQuality> requestedQualityClass;
                    if (dialogActString.equals(WHQuestion.class.getSimpleName())) {
                        requestedQualityClass = (Class<? extends TransientQuality>)
                                Ontology.thingNameMap.get(
                                        (String) groundedMeaning.newGetSlotPathFiller("verb.Patient.HasValue.class"));
                    } else {
                        Set<Object> patientRoles = ((JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Patient")).keySet();
                        Class<? extends Role> suggestedRole = null;
                        for (Object role : patientRoles) {
                            if (Ontology.roleNameMap.containsKey(role) &&
                                    HasQualityRole.class.isAssignableFrom(Ontology.roleNameMap.get(role))) {
                                suggestedRole = Ontology.roleNameMap.get(role);
                                break;
                            }
                        }
                        if (suggestedRole == null) {
                            throw new Error("no role has been suggested");
                        }
                        requestedQualityClass = Ontology.qualityInRolesRange(suggestedRole);
                    }


                    List<Class<? extends Thing>> qualityArguments = Ontology.qualityArguments(requestedQualityClass);
                    if (qualityArguments.size() != 0)
                        throw new Error("the requested quality isn't an adjective");

                    // iterate through every possible binding for the quality arguments
                    List<String> fullArgumentList = Arrays.asList(entityURI);

                    boolean dontKnow = false;
                    StringDistribution adjectiveScores = new StringDistribution();
                    Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> descriptor =
                            Ontology.qualityDescriptors(requestedQualityClass);
                    for (Class<? extends ThingWithRoles> adjectiveClass : descriptor.getRight()) {
                        Double degreeOfMatch = yodaEnvironment.db.
                                evaluateQualityDegree(fullArgumentList, adjectiveClass);
                        if (degreeOfMatch == null) {
                            dontKnow = true;
                            responseStatement.put("dialogAct", DontKnow.class.getSimpleName());
                        } else {
                            adjectiveScores.put(adjectiveClass.getSimpleName(), degreeOfMatch);
                        }
                    }

                    if (!dontKnow) {
                        Class<? extends Thing> adjectiveClass = Ontology.thingNameMap.get(adjectiveScores.getTopHypothesis());
                        if (adjectiveClass == null) {
                            responseStatement.put("dialogAct", DontKnow.class.getSimpleName());
                        } else {
                            JSONObject description = SemanticsModel.parseJSON("{\"class\":\"" + adjectiveClass.getSimpleName() + "\"}");
                            SemanticsModel.wrap(description, UnknownThingWithRoles.class.getSimpleName(),
                                    descriptor.getLeft().getSimpleName());
                            responseStatement.put("dialogAct", Statement.class.getSimpleName());
                            responseStatement.put("verb.Agent", SemanticsModel.parseJSON(Ontology.webResourceWrap(entityURI)));
                            responseStatement.put("verb.Patient", description);
                        }
                    }
                } else if (verbClass.equals(Exist.class)){
                    JSONObject searchDescription = (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Agent");
//                    System.err.println("grounded meaning:" + groundedMeaning);
                    StringDistribution recommendations = ReferenceResolution.resolveReference(yodaEnvironment, searchDescription, false, true);
//                    System.err.println("recommendations:" + recommendations);
                    String bestRecommendation = recommendations.getTopHypothesis();
                    JSONObject responseDescription = SemanticsModel.parseJSON(searchDescription.toJSONString());
                    if (bestRecommendation==null){
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
