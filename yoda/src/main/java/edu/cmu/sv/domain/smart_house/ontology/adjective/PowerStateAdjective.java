package edu.cmu.sv.domain.smart_house.ontology.adjective;

import edu.cmu.sv.domain.smart_house.ontology.quality.PowerState;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

/**
 * Created by David Cohen on 3/5/15.
 */
public abstract class PowerStateAdjective extends Adjective{
    @Override
    public Class<? extends TransientQuality> getQuality() {
        return PowerState.class;
    }
}
