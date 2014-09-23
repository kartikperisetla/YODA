package edu.cmu.sv.ontology.action;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.role.Role;

import java.util.Set;

/**
 * Created by David Cohen on 9/20/14.
 */
public abstract class Verb extends Thing{
    public abstract Set<Class <? extends Role>> getRequiredRoles();
}
