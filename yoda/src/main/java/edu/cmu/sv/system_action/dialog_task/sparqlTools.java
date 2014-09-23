package edu.cmu.sv.system_action.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/21/14.
 *
 * Class with various helper functions to generate and run SPARQL transactions, 
 * which are needed for IR / IE dialog tasks.
 *
 */
public class sparqlTools {
    /*
    * Return the set of URIs for possible referents of the description.
    * For now, this can only be a flat reference, no nested information.
    * */
    public static StringDistribution possibleReferents(Database db, SemanticsModel description){
        String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX base: <"+db.baseURI+">\n";
        String queryString = prefixes + "SELECT ?x WHERE { ";
        for (String slot : description.getSlots().keySet()){
            if (slot.equals("class"))
                queryString += "?x rdf:type base:"+description.getSlots().get(slot) + " . ";
            if (slot.equals("hasName"))
                queryString += "?x base:hasName "+description.getSlots().get(slot) + " . ";
            // todo: deal with remaining slots
        }
        queryString += "}";
//        System.out.println("query string:\n"+queryString);
        StringDistribution ans = new StringDistribution();
        db.runQuerySelectX(queryString).stream().forEach((x) -> ans.put(x, 1.0));
        ans.normalize();
        return ans;
    }

    public static boolean ynQuestionResult(Database db, SemanticsModel querySource){
        String verb = querySource.getSlots().get("verb");
        if (verb.equals("Exist")){
            StringDistribution answers = possibleReferents(db, querySource.getChildren().
                    get(querySource.getSlots().get("Patient")));
            if (answers.keySet().size() > 0)
                return true;
        }
        return false;

    }

}
