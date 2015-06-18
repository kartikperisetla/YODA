package edu.cmu.sv.domain.ontology2.query_fragments;

import edu.cmu.sv.domain.ontology2.QueryFragment;

/**
 * Created by David Cohen on 6/16/15.
 *
 * Returns 1 if the two arguments are related like this: first base:databaseProperty second
 * Returns 0 otherwise
 *
 */
public class BinaryRelationQueryFragment implements QueryFragment{
    String databaseProperty;

    public BinaryRelationQueryFragment(String databaseProperty) {
        this.databaseProperty = databaseProperty;
    }

    @Override
    public String getSparqlQueryFragment(String firstArgument, String secondArgument, String resultVariable) {
        return  "{\n"+
                firstArgument + " base:"+databaseProperty+" "+ secondArgument+" .\n"+
                "BIND (1.0 AS "+resultVariable+") \n"+
                "} UNION {\n"+
                "?x rdf:type base:Noun ."+
                "FILTER NOT EXISTS {"+firstArgument+" base:"+databaseProperty+" "+secondArgument+" }\n"+
                "BIND (0.0 AS "+resultVariable+")\n} ";
    }

//// OLD EXAMPLE from ContainedBy
//    "{\n"+
//            entityURIs.get(0) + " base:in_room "+ entityURIs.get(1)+" .\n"+
//            "BIND (1.0 AS "+entityURIs.get(2)+") \n"+
//            "} UNION {\n"+
//            "?x rdf:type base:Noun ."+
//            "FILTER NOT EXISTS {"+entityURIs.get(0)+" base:in_room "+entityURIs.get(1)+" }\n"+
//            "BIND (0.0 AS "+entityURIs.get(2)+")\n}";


}
