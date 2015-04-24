package edu.cmu.sv.domain.smart_house.ontology.role;

import edu.cmu.sv.domain.smart_house.ontology.adjective.TemperatureAdjective;
import edu.cmu.sv.domain.smart_house.ontology.noun.Room;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.has_quality_subroles.HasQualityRole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dan on 4/16/15.
 */
public class HasTemperature extends HasQualityRole {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(Room.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(TemperatureAdjective.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}

