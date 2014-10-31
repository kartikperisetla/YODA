package edu.cmu.sv.ontology.quality;

import edu.cmu.sv.ontology.modifier.Modifier;
import edu.cmu.sv.ontology.modifier.Slightly;
import edu.cmu.sv.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/31/14.
 */
public class Cheap extends Expensiveness {
    @Override
    public Class<? extends Modifier> getModifier() {
        return Slightly.class;
    }

    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return new HashSet<>();
    }
}
