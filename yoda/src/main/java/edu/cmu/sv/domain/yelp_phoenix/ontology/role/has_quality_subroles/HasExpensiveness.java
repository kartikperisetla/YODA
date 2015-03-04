package edu.cmu.sv.domain.yelp_phoenix.ontology.role.has_quality_subroles;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.ExpensivenessAdjective;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.has_quality_subroles.HasQualityRole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class HasExpensiveness extends HasQualityRole {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(PointOfInterest.class));
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
