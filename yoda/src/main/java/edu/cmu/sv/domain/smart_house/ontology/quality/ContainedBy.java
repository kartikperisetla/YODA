package edu.cmu.sv.domain.smart_house.ontology.quality;

import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

import java.util.Arrays;
import java.util.List;

/**
 * Created by David Cohen on 11/3/14.
 */
public class ContainedBy extends TransientQuality {
    static List<Class <? extends Thing>> arguments = Arrays.asList(PointOfInterest.class);

    @Override
    public List<Class<? extends Thing>> getArguments() {
        return arguments;
    }

    @Override
    public java.util.function.Function<List<String>, String> getQualityCalculatorSPARQLQuery() {

        java.util.function.Function<List<String>, String> queryGen = (List<String> entityURIs) ->
        "{\n"+
        entityURIs.get(0) + " base:in_room "+ entityURIs.get(1)+" .\n"+
        "BIND (1.0 AS "+entityURIs.get(2)+") \n"+
        "} UNION {\n"+
        "?x rdf:type base:Noun ."+
        "FILTER NOT EXISTS {"+entityURIs.get(0)+" base:in_room "+entityURIs.get(1)+" }\n"+
        "BIND (0.0 AS "+entityURIs.get(2)+")\n}";

        return queryGen;
    }
}
