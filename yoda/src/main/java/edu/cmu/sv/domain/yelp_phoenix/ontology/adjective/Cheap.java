package edu.cmu.sv.domain.yelp_phoenix.ontology.adjective;

import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class Cheap extends ExpensivenessAdjective {
    @Override
    public double getCenter() {
        return 0;
    }

    @Override
    public double getSlope() {
        return 1;
    }

    @Override
    public Set<Class<? extends Role>> getRequiredGroundedRoles() {
        return new HashSet<>();
    }
}
