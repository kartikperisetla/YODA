package edu.cmu.sv.ontology.role.has_quality_subroles;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.absolute_quality_degree.AbsoluteExpensivenessDegree;
import edu.cmu.sv.ontology.object.poi_types.Restaurant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class HasExpensiveness extends HasAbsoluteQualityDegree {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(Restaurant.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(AbsoluteExpensivenessDegree.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
