package edu.cmu.sv.domain.smart_house.ontology.adjective;

import edu.cmu.sv.domain.smart_house.ontology.quality.PowerState;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

/**
 * Created by cohend on 3/4/15.
 */
public class On extends Adjective {
    @Override
    public double getCenter() {
        return 0;
    }

    @Override
    public double getSlope() {
        return 100;
    }

    @Override
    public Class<? extends TransientQuality> getQuality() {
        return PowerState.class;
    }
}
