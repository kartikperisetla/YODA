package edu.cmu.sv.domain.yelp_phoenix.ontology.verb;

import edu.cmu.sv.domain.yelp_phoenix.ontology.role.Destination;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 12/19/14.
 */
public class GiveDirections extends Verb {
    static Set<Class <? extends Role>> requiredGroundedRoles = new HashSet<>();
    static {
        requiredGroundedRoles.add(Destination.class);
    }


    @Override
    public Set<Class<? extends Role>> getRequiredGroundedRoles() {
        return requiredGroundedRoles;
    }
}
