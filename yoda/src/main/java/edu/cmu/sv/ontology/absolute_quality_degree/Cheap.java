package edu.cmu.sv.ontology.absolute_quality_degree;

import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public class Cheap extends AbsoluteExpensivenessDegree {
    private static LexicalEntry lexicalEntry = new LexicalEntry();
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
    public Set<LexicalEntry> getLexicalEntries() {
        return super.getLexicalEntries();
    }

    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return new HashSet<>();
    }
}
