package edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality;


import edu.cmu.sv.database.PopularityFunction;
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
public class Popularity extends TransientQuality {
    static List<Class <? extends Thing>> arguments = new LinkedList<>();

    @Override
    public List<Class<? extends Thing>> getArguments() {
        return arguments;
    }


    @Override
    public Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
        Function<List<String>, String> queryGen = (List<String> entityURIs) ->
                entityURIs.get(0)+" base:yelp_review_count ?i_popularity . "+
                "BIND(base:"+ PopularityFunction.class.getSimpleName()+
                "(?i_popularity) AS "+entityURIs.get(1)+") ";

        return queryGen;
    }
}
