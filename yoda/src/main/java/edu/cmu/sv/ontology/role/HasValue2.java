package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.misc.Conjunction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/14/14.
 */
public class HasValue2 extends Role{
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(Conjunction.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(ThingWithRoles.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
