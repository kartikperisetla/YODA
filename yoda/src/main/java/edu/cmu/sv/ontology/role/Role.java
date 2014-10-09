package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.data.Data;

import java.util.Set;

/**
 * Created by David Cohen on 9/2/14.
 */
public abstract class Role extends Thing {
    /*
    * Domain and Range are the *UNION* of the set of classes in getDomain and getRange
    * */
    public abstract Set<Class <? extends Thing>> getDomain();
    public abstract Set<Class <? extends Thing>> getRange();
}
