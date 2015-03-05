package edu.cmu.sv.domain.smart_house.ontology.quality;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by David Cohen on 3/4/15.
 */
public class PowerState extends TransientQuality {
    static List<Class <? extends Thing>> arguments = Arrays.asList();

    @Override
    public Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
        java.util.function.Function<List<String>, String> queryGen = (List<String> entityURIs) ->
                entityURIs.get(0)+" base:power_state ?i_power_state . "+
                        "BIND( IF(?i_power_state = \"on\", 1.0, 0.0) AS "+entityURIs.get(1)+") ";
        return queryGen;

    }

    @Override
    public List<Class<? extends Thing>> getArguments() {
        return arguments;
    }
}
