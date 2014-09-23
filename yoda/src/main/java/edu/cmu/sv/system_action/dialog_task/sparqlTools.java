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
        String queryString = "SELECT ?x WHERE {";
        for (String slot : description.getSlots().keySet()){
            if (slot.equals("class"))
                queryString += "?x rdf:type "+description.getSlots().get(slot) + " . ";
            if (slot.equals("hasName"))
                queryString += "?x hasName "+description.getSlots().get(slot) + " . ";
            // todo: deal with remaining slots
        }
        queryString += "}";
        StringDistribution ans = new StringDistribution();
        db.runQuerySelectX(queryString).stream().forEach((x) -> ans.put(x, 1.0));
        ans.normalize();
        return ans;
    }

    public static boolean referentMatchesDescription(Database db, String referentID, SemanticsModel description){

    }

    public static boolean ynQuestionResult(Database db, String action,
                          Map<String, String> bindings,
                          Map<String, SemanticsModel> descriptions){
        if (action.equals("HasProperty")){
            return referentMatchesDescription(db, bindings.get("Agent"), descriptions.get("Patient"));
        } else if (action.equals("SameAs")){
            return bindings.get("Agent").equals(bindings.get("Patient"));
        } else {

        }
        return false;

    }

}
