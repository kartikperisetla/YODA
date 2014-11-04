package edu.cmu.sv.ontology.transient_pairwise_role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.quality.Quality;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.transient_pairwise_quality.TransientPairwiseQuality;

/**
 * Created by David Cohen on 11/3/14.
 *
 * A TransientPairwiseRole is a specific degree of a TransientPairwiseQuality
 * Ex: IsCloseTo (pairwise role) is a specific degree of Distance (pairwise quality)
 */
public abstract class TransientPairwiseRole extends Thing {
//    public abstract double getCenter();
//    public abstract double getSlope();
    public abstract Class<? extends TransientPairwiseQuality> getTransientPairwiseQuality();
}
