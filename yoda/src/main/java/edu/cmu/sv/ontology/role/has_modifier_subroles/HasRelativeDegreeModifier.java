package edu.cmu.sv.ontology.role.has_modifier_subroles;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.modifier.RelativeDegreeModifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class HasRelativeDegreeModifier extends HasModifier {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(Adjective.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(RelativeDegreeModifier.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }
}