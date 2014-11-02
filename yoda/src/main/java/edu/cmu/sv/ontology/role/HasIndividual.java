package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/1/14.
 */
public class HasIndividual extends Role {
    static Set<Class<? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(edu.cmu.sv.ontology.object.Object.class));
    static Set<Class<? extends Thing>> range = new HashSet<>(Arrays.asList(WebResource.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
