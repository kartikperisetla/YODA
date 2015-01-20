package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.noun.Person;
import edu.cmu.sv.ontology.verb.HasProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/20/14.
 */
public class Patient extends Role {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(HasProperty.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(Person.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
