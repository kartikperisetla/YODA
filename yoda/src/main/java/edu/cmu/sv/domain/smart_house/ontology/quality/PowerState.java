package edu.cmu.sv.domain.smart_house.ontology.quality;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

import java.util.List;
import java.util.function.Function;

/**
 * Created by cohend on 3/4/15.
 */
public class PowerState extends TransientQuality {
    @Override
    public Function<List<String>, String> getQualityCalculatorSPARQLQuery() {
        return null;
    }

    @Override
    public List<Class<? extends Thing>> getArguments() {
        return null;
    }
}
