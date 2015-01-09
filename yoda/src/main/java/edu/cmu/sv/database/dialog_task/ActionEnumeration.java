package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.Combination;
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
    public static enum FOCUS_CONSTRAINT {IN_FOCUS, IN_KB}

    public static Set<Map<String, Object>> getPossibleIndividualBindings(DialogAct dialogAct,
                                                                         YodaEnvironment yodaEnvironment,
                                                                         FOCUS_CONSTRAINT focusConstraint){

        if (dialogAct.getIndividualParameters().size()==0){
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
            if (focusConstraint==FOCUS_CONSTRAINT.IN_FOCUS)
                focusConstraintString += "?" + parameter + " rdf:type dst:InFocus .\n";
        }
        String queryString = Database.prefixes + "SELECT DISTINCT "+variableEnumerationString+"WHERE {\n";
        queryString += focusConstraintString;
        queryString += classConstraintString;
        queryString += "}";
        yodaEnvironment.db.log(queryString);
        Database.getLogger().info("Action enumeration query:\n"+queryString);

        Set<Map<String, Object>> ans = new HashSet<>();
        try {
            TupleQuery query = yodaEnvironment.db.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = query.evaluate();

            while (result.hasNext()){
                Map<String, Object> binding = new HashMap<>();
                BindingSet bindings = result.next();
                for (String variable: bindings.getBindingNames()){
                    binding.put(variable, bindings.getValue(variable).stringValue());
                }
                ans.add(binding);
            }
            result.close();
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
            e.printStackTrace();
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
            Class<? extends Role> lastRole = OntologyRegistry.roleNameMap.get(roles[roles.length-1]);
            for (Class<? extends Thing> thingClass : OntologyRegistry.thingNameMap.values()){
                if (OntologyRegistry.inRange(lastRole, thingClass)){
                    JSONObject description = SemanticsModel.parseJSON("{\"class\":\""+thingClass.getSimpleName()+"\"}");
                    ans.add(description);
                    // todo: by name

                    // todo: adjectives

                    // todo: prepositions
                }
            }
        }
//        System.out.println("getPossibleGivenDescriptions result:"+ans);
        return ans;
    }

    public static Set<Map<String, Object>> getPossibleNonIndividualBindings(DialogAct dialogAct,
                                                                            DiscourseUnit contextDiscourseUnit){
        Set<Map<String, Object>> ans = new HashSet<>();
        String verbConstraint = null;
        if (contextDiscourseUnit!=null)
            verbConstraint = (String) contextDiscourseUnit.getFromInitiator("verb.class");

        for (Class<? extends Verb> verbClass : OntologyRegistry.verbClasses) {
            if (verbConstraint != null && !OntologyRegistry.thingNameMap.get(verbConstraint).equals(verbClass))
                continue;
            Map<String, Set<Object>> possibleBindingsPerVariable = new HashMap<>();
            possibleBindingsPerVariable.put("verb_class", new HashSet<>(Arrays.asList(verbClass.getSimpleName())));

            if (dialogAct.getPathParameters().containsKey("given_role_path")) {
                possibleBindingsPerVariable.put("given_role_path",
                        OntologyRegistry.roleClasses.stream().
                                filter(x -> OntologyRegistry.inDomain(x, verbClass)).
                                map(x -> "verb." + x.getSimpleName()).
                                collect(Collectors.toSet()));
            }
            if (dialogAct.getPathParameters().containsKey("requested_role_path")) {
                possibleBindingsPerVariable.put("requested_role_path",
                        OntologyRegistry.roleClasses.stream().
                                filter(x -> OntologyRegistry.inDomain(x, verbClass)).
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
