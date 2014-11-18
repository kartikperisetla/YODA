package edu.cmu.sv.ontology.quality.binary_quality;

import edu.cmu.sv.database.DistanceFunction;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.quality.TransientQuality;

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
                entityURIs.get(0)+" base:gps_lat ?i ; base:gps_lon ?j . "+
                        entityURIs.get(1)+" base:gps_lat ?k ; base:gps_lon ?l . "+
                        "BIND(base:"+ DistanceFunction.class.getSimpleName()+
                        "(?i, ?j, ?k, ?l) AS "+entityURIs.get(2)+") ";

        return queryGen;
    }
}
