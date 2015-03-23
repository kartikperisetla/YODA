package edu.cmu.sv.domain.smart_house.ontology.role;

import edu.cmu.sv.domain.smart_house.ontology.noun.Room;
import edu.cmu.sv.domain.smart_house.ontology.verb.CleanRoom;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 3/5/15.
 */
public class HasRoom extends Role {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(CleanRoom.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(Room.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
