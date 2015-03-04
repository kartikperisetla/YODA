package edu.cmu.sv.domain.yelp_phoenix.ontology.quality.binary_quality;

import edu.cmu.sv.database.DistanceFunction;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

import java.util.*;

/**
 * Created by David Cohen on 11/3/14.
 */
public class Distance extends TransientQuality {
    static List<Class <? extends Thing>> arguments = Arrays.asList(PointOfInterest.class);

    @Override
    public List<Class<? extends Thing>> getArguments() {
        return arguments;
    }

    @Override
    public java.util.function.Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
        java.util.function.Function<List<String>, String> queryGen = (List<String> entityURIs) ->
                entityURIs.get(0)+" base:gps_lat ?i_position ; base:gps_lon ?j_position . "+
                        entityURIs.get(1)+" base:gps_lat ?k_position ; base:gps_lon ?l_position . "+
//                        "FILTER(ABS(?i_position - ?k_position) < .05) " +
//                        "FILTER(ABS(?j_position - ?l_position) < .05) " +
                        "BIND(base:"+ DistanceFunction.class.getSimpleName()+
                        "(?i_position, ?j_position, ?k_position, ?l_position) AS "+entityURIs.get(2)+") ";

        return queryGen;
    }
}
