package edu.cmu.sv.ontology;


import edu.cmu.sv.natural_language_generation.LexicalEntry;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/23/14.
 */
public abstract class Thing {
    public Set<LexicalEntry> getLexicalEntries(){return new HashSet<>();}
}
