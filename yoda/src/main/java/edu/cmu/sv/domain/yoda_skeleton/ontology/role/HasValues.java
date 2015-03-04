package edu.cmu.sv.domain.yoda_skeleton.ontology.role;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.Conjunction;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/14/14.
 *
 * The role used by conjunctions to contain the set of values.
 *
 * {class: And,
 *  HasValues: [{...}, {...}]}
 *
 */
public class HasValues extends Role {
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
