package edu.cmu.sv.ontology.misc;

import edu.cmu.sv.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/20/14.
 */
public class And extends Conjunction {
    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return new HashSet<>();
    }
}
