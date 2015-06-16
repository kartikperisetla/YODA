package edu.cmu.sv.domain.ontology2;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 6/16/15.
 */
public class Verb2 {
    public String name;
    Set<Role2> requiredGroundedRoles = new HashSet<>();
    Set<Role2> requiredDescriptions = new HashSet<>();

    public Verb2(String name, Collection<Role2> requiredGroundedRoles, Collection<Role2> requiredDescriptions) {
        this.name = name;
        this.requiredGroundedRoles.addAll(requiredGroundedRoles);
        this.requiredDescriptions.addAll(requiredDescriptions);
    }

    public Set<Role2> getRequiredGroundedRoles() {
        return requiredGroundedRoles;
    }

    public Set<Role2> getRequiredDescriptions() {
        return requiredDescriptions;
    }
}
