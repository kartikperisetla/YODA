package edu.cmu.sv.ontology.quality.unary_quality;


import edu.cmu.sv.database.Database;
import edu.cmu.sv.database.DistanceFunction;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.Role;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;

/**
 * Created by David Cohen on 10/31/14.
 *
 * The quality of being expensive
 *
 */
public class Expensiveness extends TransientQuality {
    static List<Class <? extends Thing>> arguments = new LinkedList<>();

    @Override
    public List<Class<? extends Thing>> getArguments() {
        return arguments;
    }


    @Override
    public Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
        java.util.function.Function<List<String>, String> queryGen = (List<String> entityURIs) ->
                entityURIs.get(0)+" base:expensiveness "+entityURIs.get(1)+" . ";
        return queryGen;
    }
}
