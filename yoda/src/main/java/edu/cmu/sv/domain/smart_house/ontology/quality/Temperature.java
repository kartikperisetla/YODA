package edu.cmu.sv.domain.smart_house.ontology.quality;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by dan on 4/15/15.
 */
public class Temperature extends TransientQuality {
    static List<Class <? extends Thing>> arguments = Arrays.asList();

    @Override
    public Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
        Function<List<String>, String> queryGen = (List<String> entityURIs) ->
                entityURIs.get(0)+" base:temperature ?i_temperature . "+
                        "BIND( ?i_temperature / 120 AS "+entityURIs.get(1)+") ";
        return queryGen;

    }

    @Override
    public List<Class<? extends Thing>> getArguments() {
        return arguments;
    }
}
