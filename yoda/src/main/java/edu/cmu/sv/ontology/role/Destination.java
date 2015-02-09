package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.verb.GiveDirections;
import edu.cmu.sv.ontology.verb.MakeReservation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 12/19/14.
 */
public class Destination extends Role {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(MakeReservation.class, GiveDirections.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(PointOfInterest.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
