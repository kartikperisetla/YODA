package edu.cmu.sv.ontology.quality.unary_quality;


import edu.cmu.sv.database.GoodnessFunction;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.quality.TransientQuality;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by David Cohen on 10/31/14.
 *
 * The quality of being expensive
 *
 */
public class Goodness extends TransientQuality {
    static List<Class <? extends Thing>> arguments = new LinkedList<>();

    @Override
    public List<Class<? extends Thing>> getArguments() {
        return arguments;
    }


    @Override
    public Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
        Function<List<String>, String> queryGen = (List<String> entityURIs) ->
                entityURIs.get(0)+" base:yelp_stars ?i_stars . "+
                "BIND(base:"+ GoodnessFunction.class.getSimpleName()+
                "(?i_stars) AS "+entityURIs.get(1)+") ";

        return queryGen;
    }
}
