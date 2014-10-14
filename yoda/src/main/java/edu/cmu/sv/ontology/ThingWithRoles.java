package edu.cmu.sv.ontology;

import edu.cmu.sv.ontology.role.Role;

import java.util.Set;

/**
 * Created by David Cohen on 10/14/14.
 *
 * Things with Roles include Nouns, Verbs, and several Misc Classes
 *
 */
public abstract class ThingWithRoles extends Thing {
    public abstract Set<Class <? extends Role>> getRequiredRoles();

}
