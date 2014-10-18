package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.misc.Conjunction;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.misc.Suggested;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/17/14.
 */
public class HasValue extends Role {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(Suggested.class, Requested.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(Thing.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
