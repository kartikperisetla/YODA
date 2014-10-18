package edu.cmu.sv.ontology.misc;

import edu.cmu.sv.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/16/14.
 */
public class Suggested extends DiscourseMarker {
    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return new HashSet<>();
    }
}
