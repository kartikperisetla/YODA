package edu.cmu.sv.ontology.preposition;

import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/3/14.
 *
 * A TransientPairwiseRole is a specific degree of a TransientPairwiseQuality
 * Ex: IsCloseTo (pairwise role) is a specific degree of Distance (pairwise quality)
 */
public abstract class Preposition extends ThingWithRoles{
    static Set<Class<? extends Role>> requiredRoles = new HashSet<>();
    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return requiredRoles;
    }
    public abstract double getCenter();
    public abstract double getSlope();
    public abstract Class<? extends TransientQuality> getQuality();
}
