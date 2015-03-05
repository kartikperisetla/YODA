package edu.cmu.sv.domain.smart_house.ontology.role;

import edu.cmu.sv.domain.smart_house.ontology.adjective.PowerStateAdjective;
import edu.cmu.sv.domain.smart_house.ontology.noun.Appliance;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.has_quality_subroles.HasQualityRole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 3/5/15.
 */
public class HasPowerState extends HasQualityRole {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(Appliance.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(PowerStateAdjective.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
