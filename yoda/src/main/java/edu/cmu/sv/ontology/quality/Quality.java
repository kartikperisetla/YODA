package edu.cmu.sv.ontology.quality;

import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.modifier.Modifier;
import edu.cmu.sv.ontology.role.Role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/31/14.
 */
public abstract class Quality extends ThingWithRoles {
    // individual quality instances have lexical information attached to them
    public LexicalEntry lexicalEntry;

    @Override
    public Set<LexicalEntry> getLexicalEntries() {
        return new HashSet<>(Arrays.asList(lexicalEntry));
    }
}
