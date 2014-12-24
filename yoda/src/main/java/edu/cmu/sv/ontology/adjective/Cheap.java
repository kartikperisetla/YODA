package edu.cmu.sv.ontology.adjective;

import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.ontology.role.Role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class Cheap extends ExpensivenessAdjective {
    private static Lexicon.LexicalEntry lexicalEntry = new Lexicon.LexicalEntry();
    static {
        lexicalEntry.adjectives.add("cheap");
    }

    @Override
    public double getCenter() {
        return 0;
    }

    @Override
    public double getSlope() {
        return 1;
    }

    @Override
    public Set<Lexicon.LexicalEntry> getLexicalEntries() {
        return new HashSet<>(Arrays.asList(lexicalEntry));
    }

    @Override
    public Set<Class<? extends Role>> getRequiredGroundedRoles() {
        return new HashSet<>();
    }
}
