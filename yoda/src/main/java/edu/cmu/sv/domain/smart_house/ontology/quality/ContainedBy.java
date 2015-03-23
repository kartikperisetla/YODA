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
//            System.out.println(entityURIs);
//            entityURIs.get(0) + " base:in_room " + entityURIs.get(1) + " . " +
////                        "FILTER (isURI (?i_in_room))"+
//                    "BIND (1 as " + entityURIs.get(2) + ")";
////                        "BIND ( IF ( ?i_in_room = "+entityURIs.get(1)+" , 1.0, 0.0 ) AS "+entityURIs.get(2)+")";


                "{{ OPTIONAL {"+entityURIs.get(0)+" base:in_room "+entityURIs.get(1)+" . " +
                "BIND (1.0 AS "+entityURIs.get(2)+")}\n" +
                "UNION\n" +
                "{OPTIONAL { BIND (0.0 AS "+entityURIs.get(2)+")}}\n" +
                "}}\n";

//                entityURIs.get(0)+" base:in_room ?i_in_room . " +
//                "BIND( IF( ?i_in_room = "+entityURIs.get(1)+", 1.0, 0.0) AS "+entityURIs.get(2)+")";

        return queryGen;
    }
}
