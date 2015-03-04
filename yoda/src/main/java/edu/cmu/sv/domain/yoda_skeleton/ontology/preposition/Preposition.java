package edu.cmu.sv.domain.yoda_skeleton.ontology.preposition;

import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

/**
 * Created by David Cohen on 11/3/14.
 *
 * A TransientPairwiseRole is a specific degree of a TransientPairwiseQuality
 * Ex: IsCloseTo (pairwise role) is a specific degree of Distance (pairwise quality)
 */
public abstract class Preposition extends ThingWithRoles{

    public abstract double getCenter();
    public abstract double getSlope();
    public abstract Class<? extends TransientQuality> getQuality();
}
