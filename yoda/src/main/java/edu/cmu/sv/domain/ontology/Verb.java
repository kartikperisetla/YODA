package edu.cmu.sv.domain.ontology;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 6/16/15.
 */
public class Verb {
    public String name;
    Set<Role> requiredGroundedRoles = new HashSet<>();
    Set<Role> requiredDescriptions = new HashSet<>();

    public Verb(String name, Collection<Role> requiredGroundedRoles, Collection<Role> requiredDescriptions) {
        this.name = name;
        this.requiredGroundedRoles.addAll(requiredGroundedRoles);
        this.requiredDescriptions.addAll(requiredDescriptions);
    }

    public Set<Role> getRequiredGroundedRoles() {
        return requiredGroundedRoles;
    }

    public Set<Role> getRequiredDescriptions() {
        return requiredDescriptions;
    }
}
