package edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality;


import edu.cmu.sv.database.PriceRangeFunction;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

import java.util.LinkedList;
import java.util.List;
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
                entityURIs.get(0)+" base:PriceRange ?i_price_range . "+
                "BIND(base:"+ PriceRangeFunction.class.getSimpleName()+
                "(?i_price_range) AS "+entityURIs.get(1)+") ";

        return queryGen;
    }
}
