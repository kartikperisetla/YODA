package edu.cmu.sv.ontology.preposition;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.quality.binary_quality.TransientPairwiseQuality;

import java.util.Set;

/**
 * Created by David Cohen on 11/3/14.
 *
 * A TransientPairwiseRole is a specific degree of a TransientPairwiseQuality
 * Ex: IsCloseTo (pairwise role) is a specific degree of Distance (pairwise quality)
 */
public abstract class TransientPairwiseRole extends Role{
    public abstract double getCenter();
    public abstract double getSlope();
    public abstract TransientPairwiseQuality getTransientPairwiseQuality();

    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return getTransientPairwiseQuality().getDomain();
    }

    public Set<Class<? extends Thing>> getRange() {
        return getTransientPairwiseQuality().getRange();
    }
}
