package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;

import java.util.Set;

/**
 * Created by David Cohen on 9/2/14.
 */
public abstract class Role extends Thing {
    /*
    * Domain and Range are the *UNION* of the set of classes in getDomain and getRange
    *
    * Domain and range *MUST* be based on static variables within each class, so that they can be modified
    * inside the OntologyRegistry
    *
    * */
    public abstract Set<Class <? extends ThingWithRoles>> getDomain();
    public abstract Set<Class <? extends Thing>> getRange();
}
