package edu.cmu.sv.domain.smart_house.ontology.adjective;

import edu.cmu.sv.domain.smart_house.ontology.quality.Temperature;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

/**
 * Created by dan on 4/15/15.
 */
public abstract class TemperatureAdjective extends Adjective {
    @Override
    public Class<? extends TransientQuality> getQuality() {
        return Temperature.class;
    }
}
