package edu.cmu.sv.ontology.action;

import edu.cmu.sv.ontology.role.Patient;
import edu.cmu.sv.ontology.role.Role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/23/14.
 */
public class Exist extends Verb {
    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return new HashSet<>(Arrays.asList(Patient.class));
    }
}
