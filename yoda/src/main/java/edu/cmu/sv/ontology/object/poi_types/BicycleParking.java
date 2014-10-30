package edu.cmu.sv.ontology.object.poi_types;

import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.ontology.object.PointOfInterest;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/29/14.
 */
public class BicycleParking extends PointOfInterest {
    static Set<LexicalEntry> lexicalEntries = new HashSet<>();
    static {
        LexicalEntry e1 = new LexicalEntry();
        e1.singularNounForms.add("bike rack");
        lexicalEntries.add(e1);
    }

    @Override
    public Set<LexicalEntry> getLexicalEntries() {
        return lexicalEntries;
    }
}
