package edu.cmu.sv.database;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/17/14.
 */
public class ReferenceResolution {

    /*
    * return a distribution over URI's that this JSONObject may refer to
    * */
    public static StringDistribution resolveReference(YodaEnvironment yodaEnvironment, JSONObject reference){
        String queryString = yodaEnvironment.db.prefixes + "SELECT ?x0 ?score0 WHERE {\n";
        queryString += referenceResolutionHelper(reference, 0).getKey();
        queryString += "} \nORDER BY DESC(?score0) \nLIMIT 10";


        yodaEnvironment.db.log(queryString);

        System.out.println(queryString);

        StringDistribution ans = new StringDistribution();
        try {
            TupleQuery query = yodaEnvironment.db.connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = query.evaluate();

            while (result.hasNext()){
                BindingSet bindings = result.next();
                ans.put(bindings.getValue("x0").stringValue(),
                        Double.parseDouble(bindings.getValue("score0").stringValue()));
            }
            result.close();
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException e) {
            e.printStackTrace();
        }

        System.out.println("result:\n"+ans);
        return ans;
    }


    /*
    * Return a partial query string and an updated tmpVarIndex for the reference JSONObject
    * tmpVarIndex is used so that temporary variables within the query don't have naming conflicts
    * */
    private static Pair<String, Integer> referenceResolutionHelper(JSONObject reference, Integer tmpVarIndex){
        String ans = "";
        if (reference.get("class").equals(WebResource.class.getSimpleName())){
            ans += "?x"+tmpVarIndex+" rdfs:label ?tmp"+tmpVarIndex+" . \n" +
                    "base:"+reference.get(HasURI.class.getSimpleName())+" rdf:value ?tmpV"+tmpVarIndex+" . \n" +
                    "BIND( base:"+StringSimilarity.class.getSimpleName()+
                    "(?tmp"+tmpVarIndex+", ?tmpV"+tmpVarIndex+") AS ?score"+tmpVarIndex+")\n";
            tmpVarIndex ++;
        } else {
            throw new Error("can only resolve references of class WebResource");
        }
        return new ImmutablePair<>(ans, tmpVarIndex);
    }

}
