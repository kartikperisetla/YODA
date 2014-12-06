package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 12/6/14.
 */
public class ActionEnumeration {

    //todo: also look for classes in context
    public static Set<Map<String, Object>> getPossibleBindings(DialogAct dialogAct,
                                                         DiscourseUnitHypothesis contextDiscourseUnitHypothesis){

        Set<String> individualsInContext = new HashSet<>();
        if (contextDiscourseUnitHypothesis.getGroundTruth()!=null)
            individualsInContext.addAll(contextDiscourseUnitHypothesis.getGroundTruth().
                findAllPathsToClass(WebResource.class.getSimpleName()));
        if (contextDiscourseUnitHypothesis.getGroundInterpretation()!=null)
            individualsInContext.addAll(contextDiscourseUnitHypothesis.getGroundInterpretation().
                    findAllPathsToClass(WebResource.class.getSimpleName()));

        String variableEnumerationString = "";
        String classConstraintString = "";
        String unionOfIndividualsString = "";
        for (String parameter : dialogAct.getIndividualParameters().keySet()) {
            variableEnumerationString += "?" + parameter + " ";
            classConstraintString += "?" + parameter + " rdf:type base:" + dialogAct.getIndividualParameters().get(parameter).getSimpleName() + " .\n";
            unionOfIndividualsString += String.join(" UNION ",
                    individualsInContext.stream().map(URI -> "{ ?" + parameter + " = <" + URI + "> }").collect(Collectors.toList()));
        }
        String sparqlQuery = Database.prefixes + "SELECT DISTINCT "+variableEnumerationString+" WHERE {\n";
        sparqlQuery += unionOfIndividualsString;
        sparqlQuery += classConstraintString;
        sparqlQuery += "}";
        System.out.println(sparqlQuery);

        return null;
    }



}
