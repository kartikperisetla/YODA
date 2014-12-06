package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 12/6/14.
 */
public class ActionEnumeration {

    //todo: also look for classes in context
    public static Set<Map<String, Object>> getPossibleBindings(DialogAct dialogAct,
                                                         DiscourseUnitHypothesis contextDiscourseUnitHypothesis,
                                                         YodaEnvironment yodaEnvironment){

        Set<String> individualsInContext = new HashSet<>();
        if (contextDiscourseUnitHypothesis.getGroundTruth()!=null) {
            Set<String> paths = contextDiscourseUnitHypothesis.getGroundTruth().
                    findAllPathsToClass(WebResource.class.getSimpleName());
            for (String path : paths){
                individualsInContext.add((String)contextDiscourseUnitHypothesis.getGroundTruth().
                        newGetSlotPathFiller(path+"."+ HasURI.class.getSimpleName()));
            }
        }
        if (contextDiscourseUnitHypothesis.getGroundInterpretation()!=null) {
            Set<String> paths = contextDiscourseUnitHypothesis.getGroundInterpretation().
                    findAllPathsToClass(WebResource.class.getSimpleName());
            for (String path : paths){
                individualsInContext.add((String)contextDiscourseUnitHypothesis.getGroundInterpretation().
                        newGetSlotPathFiller(path+"."+ HasURI.class.getSimpleName()));
            }
        }

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



}
