package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.ontology.Ontology;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.Combination;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 12/6/14.
 */
public class ActionEnumeration {
    public static int maxOntologyBindings = 2;
    public static int maxIndividualBindings = 10;

    public static enum FOCUS_CONSTRAINT {IN_FOCUS, IN_KB}
    public static enum ENUMERATION_TYPE {EXHAUSTIVE, SAMPLED}

    public static FOCUS_CONSTRAINT focusConstraint;
    public static ENUMERATION_TYPE enumerationType;

    public static Set<Map<String, Object>> getPossibleIndividualBindings(DialogAct dialogAct,
                                                                         YodaEnvironment yodaEnvironment) {
        if (dialogAct.getIndividualParameters().size() == 0) {
            Set<Map<String, Object>> ans = new HashSet<>();
            ans.add(new HashMap<>());
            return ans;
        }

        String variableEnumerationString = "";
        String classConstraintString = "";
        String focusConstraintString = "";
        for (String parameter : dialogAct.getIndividualParameters().keySet()) {
            variableEnumerationString += "?" + parameter + " ";
            classConstraintString += "?" + parameter + " rdf:type base:" + dialogAct.getIndividualParameters().get(parameter).getSimpleName() + " .\n";
            if (focusConstraint == FOCUS_CONSTRAINT.IN_FOCUS)
                focusConstraintString += "?" + parameter + " rdf:type dst:InFocus .\n";
        }
        String queryString = Database.prefixes + "SELECT DISTINCT " + variableEnumerationString + "WHERE {\n";
        queryString += focusConstraintString;
        queryString += classConstraintString;
        queryString += "}";
        if (enumerationType.equals(ENUMERATION_TYPE.SAMPLED))
            queryString += " LIMIT " + maxIndividualBindings;

        Set<Map<String, Object>> ans = new HashSet<>();

        synchronized (yodaEnvironment.db.connection) {
            yodaEnvironment.db.log(queryString);
            Database.getLogger().info(MongoLogHandler.createSimpleRecord("action enumeration query", queryString).toJSONString());

            try {
                TupleQuery query = yodaEnvironment.db.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
                TupleQueryResult result = query.evaluate();

                while (result.hasNext()) {
                    Map<String, Object> binding = new HashMap<>();
                    BindingSet bindings = result.next();
                    for (String variable : bindings.getBindingNames()) {
                        binding.put(variable, bindings.getValue(variable).stringValue());
                    }
                    ans.add(binding);
                }
                result.close();
            } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        return ans;
    }


    public static Set<Object> getPossibleGivenDescriptions(DiscourseUnit contextDiscourseUnit,
                                                           String path){
        Set<Object> ans = new HashSet<>();
        if (contextDiscourseUnit!=null){
            if (contextDiscourseUnit.getFromInitiator(path)!=null)
                ans.add(contextDiscourseUnit.getFromInitiator(path));
        } else {
            String[] roles = path.split("\\.");
            Class<? extends Role> lastRole = Ontology.roleNameMap.get(roles[roles.length-1]);
            for (Class<? extends Thing> thingClass : Ontology.thingNameMap.values()){
                if (Ontology.inRange(lastRole, thingClass)){
                    JSONObject description = SemanticsModel.parseJSON("{\"class\":\""+thingClass.getSimpleName()+"\"}");
                    ans.add(description);
                    // todo: by name

                    // todo: adjectives

                    // todo: prepositions
                }
            }
        }
        return Combination.randomSubset(ans, maxOntologyBindings);
    }

    public static Set<Map<String, Object>> getPossibleNonIndividualBindings(DialogAct dialogAct,
                                                                            DiscourseUnit contextDiscourseUnit){
        Set<Map<String, Object>> ans = new HashSet<>();
        String verbConstraint = null;
        if (contextDiscourseUnit!=null)
            verbConstraint = (String) contextDiscourseUnit.getFromInitiator("verb.class");

//        Set<Class<? extends Verb>> verbClassSet;
//        if (enumerationType.equals(ENUMERATION_TYPE.SAMPLED)){
//            verbClassSet = Combination.randomSubset(Ontology.verbClasses, maxOntologyBindings);
//        } else {
//            verbClassSet = Ontology.verbClasses;
//        }

        for (Class<? extends Verb> verbClass : Ontology.verbClasses) {
            if (verbConstraint != null && !Ontology.thingNameMap.get(verbConstraint).equals(verbClass))
                continue;
            if (verbClass==Verb.class)
                continue;
            Map<String, Set<Object>> possibleBindingsPerVariable = new HashMap<>();
            possibleBindingsPerVariable.put("verb_class", new HashSet<>(Arrays.asList(verbClass.getSimpleName())));

            if (dialogAct.getPathParameters().containsKey("given_role_path")) {
                possibleBindingsPerVariable.put("given_role_path",
                        Ontology.roleClasses.stream().
                                filter(x -> Ontology.inDomain(x, verbClass)).
                                map(x -> "verb." + x.getSimpleName()).
                                collect(Collectors.toSet()));
            }
            if (dialogAct.getPathParameters().containsKey("requested_role_path")) {
                possibleBindingsPerVariable.put("requested_role_path",
                        Ontology.roleClasses.stream().
                                filter(x -> Ontology.inDomain(x, verbClass)).
                                map(x -> "verb." + x.getSimpleName()).
                                collect(Collectors.toSet()));
            }

            // add variables to bindings which are dependent on already bound variables
            Set<Map<String, Object>> possibleBindings = Combination.possibleBindings(possibleBindingsPerVariable);
            for (Map<String, Object> binding : possibleBindings) {
                // given_role_description -> given_role_path must be given
                if (dialogAct.getDescriptionParameters().containsKey("given_role_description")) {
                    try {
                        Assert.verify(binding.containsKey("given_role_path"));
                    } catch (Assert.AssertException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                    for (Object givenRoleDescription : getPossibleGivenDescriptions(contextDiscourseUnit,
                            (String) binding.get("given_role_path"))){
                        Map<String, Object> updatedBinding = new HashMap<>();
                        updatedBinding.putAll(binding);
                        updatedBinding.put("given_role_description", givenRoleDescription);
                        ans.add(updatedBinding);
                    }
                } else {
                    ans.add(binding);
                }
            }
        }
        return ans.stream().
                filter(x -> dialogAct.getPathParameters().keySet().stream().allMatch(x::containsKey)).
                filter(x -> dialogAct.getDescriptionParameters().keySet().stream().allMatch(x::containsKey)).
                filter(x -> dialogAct.getClassParameters().keySet().stream().allMatch(x::containsKey)).
                collect(Collectors.toSet());
    }




}
