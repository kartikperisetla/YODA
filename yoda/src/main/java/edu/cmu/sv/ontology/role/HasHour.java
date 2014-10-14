package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.URI;
import edu.cmu.sv.ontology.object.Meeting;
import edu.cmu.sv.ontology.object.Time;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/13/14.
 */
public class HasHour extends Role {
    static Set<Class <? extends Thing>> domain = new HashSet<>(Arrays.asList(Time.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(URI.class));

    @Override
    public Set<Class<? extends Thing>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }

}