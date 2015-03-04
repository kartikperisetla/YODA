package edu.cmu.sv.domain.yelp_phoenix.ontology.preposition;

import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.binary_quality.Distance;

/**
 * Created by David Cohen on 11/4/14.
 */
public abstract class DistancePreposition extends Preposition {
    @Override
    public Class<? extends TransientQuality> getQuality() {
        return Distance.class;
    }
}
