package edu.cmu.sv.ontology.preposition;

import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.quality.binary_quality.Distance;

/**
 * Created by David Cohen on 11/4/14.
 */
public abstract class DistancePreposition extends Preposition{
    @Override
    public Class<? extends TransientQuality> getQuality() {
        return Distance.class;
    }
}
