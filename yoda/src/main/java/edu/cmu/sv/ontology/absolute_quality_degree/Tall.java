package edu.cmu.sv.ontology.absolute_quality_degree;

import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.ontology.quality.Height;
import edu.cmu.sv.ontology.quality.Quality;
import edu.cmu.sv.ontology.role.Role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class Tall extends AbsoluteHeightDegree {
    private static LexicalEntry lexicalEntry = new LexicalEntry();
    static {
        lexicalEntry.adjectives.add("tall");
    }

    @Override
    public Set<LexicalEntry> getLexicalEntries() {
        return new HashSet<>(Arrays.asList(lexicalEntry));
    }

    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return new HashSet<>();
    }

    @Override
    public double getCenter() {
        return 1;
    }

    @Override
    public double getSlope() {
        return 1;
    }
}
