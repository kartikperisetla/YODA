package edu.cmu.sv.domain.smart_house.ontology.preposition;

import edu.cmu.sv.domain.smart_house.ontology.quality.ContainedBy;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

/**
 * Created by David Cohen on 11/4/14.
 */
public abstract class ContainedByPreposition extends Preposition {
    @Override
    public Class<? extends TransientQuality> getQuality() {
        return ContainedBy.class;
    }
}
