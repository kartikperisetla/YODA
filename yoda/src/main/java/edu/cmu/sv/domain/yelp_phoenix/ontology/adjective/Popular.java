package edu.cmu.sv.domain.yelp_phoenix.ontology.adjective;

import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class Popular extends PopularityAdjective {
    @Override
    public double getCenter() {
        return 1;
    }

    @Override
    public double getSlope() {
        return 3;
    }

    @Override
    public Set<Class<? extends Role>> getRequiredGroundedRoles() {
        return new HashSet<>();
    }
}
