package edu.cmu.sv.ontology.role;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.data.*;
import edu.cmu.sv.ontology.object.Person;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/20/14.
 */
public class HasName extends Role {
    static Set<Class <? extends Thing>> domain = new HashSet<>(Arrays.asList(Person.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(edu.cmu.sv.ontology.data.String.class));

    @Override
    public Set<Class<? extends Thing>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}
