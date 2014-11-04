package edu.cmu.sv.ontology.quality.binary_quality;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.database.DistanceFunction;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.quality.TransientQuality;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by David Cohen on 11/3/14.
 */
public class HasDistance extends TransientQuality {
    @Override
    public java.util.function.Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
        java.util.function.Function<List<String>, String> queryGen = (List<String> entityURIs) ->
                Database.prefixes+"SELECT ?transient_quality WHERE { "+
                        "<"+entityURIs.get(0)+"> base:gps_lat ?i ; base:gps_lon ?j . "+
                        "<"+entityURIs.get(1)+"> base:gps_lat ?k ; base:gps_lon ?l . "+
                        "BIND(base:"+ DistanceFunction.class.getSimpleName()+
                        "(?i, ?j, ?k, ?l) AS ?transient_quality) }";

        return queryGen;
    }
}
