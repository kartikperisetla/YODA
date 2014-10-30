package edu.cmu.sv.ontology.role;

import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.object.PointOfInterest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/30/14.
 *
 * Relates two POI's that are geographically close to each other.
 */
public class IsCloseTo extends Role {

    static Set<LexicalEntry> lexicalEntries = new HashSet<>();
    static {
        LexicalEntry e1 = new LexicalEntry();
        e1.relationalPrepositionalPhrases.add("near to");
        e1.relationalPrepositionalPhrases.add("near");
        e1.relationalPrepositionalPhrases.add("close to");
        lexicalEntries.add(e1);
    }

    @Override
    public Set<LexicalEntry> getLexicalEntries() {
        return lexicalEntries;
    }

    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(PointOfInterest.class));
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
