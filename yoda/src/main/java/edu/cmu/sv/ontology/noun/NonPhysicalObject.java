package edu.cmu.sv.ontology.noun;

import edu.cmu.sv.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/20/14.
 */
public class NonPhysicalObject extends Object {
    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return new HashSet<>();
    }
}
