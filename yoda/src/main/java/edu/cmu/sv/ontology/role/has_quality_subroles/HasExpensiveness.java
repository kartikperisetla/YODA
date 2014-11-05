package edu.cmu.sv.ontology.role.has_quality_subroles;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.adjective.ExpensivenessAdjective;
import edu.cmu.sv.ontology.noun.poi_types.Restaurant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class HasExpensiveness extends HasQualityRole {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(Restaurant.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(ExpensivenessAdjective.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
