package edu.cmu.sv.ontology.action;

import edu.cmu.sv.ontology.role.Role;

import java.util.Set;

/**
 * Created by David Cohen on 9/20/14.
 */
public interface Verb {

    public Set<Class <? extends Role>> getRequiredRoles();

}
