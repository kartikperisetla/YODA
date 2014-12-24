package edu.cmu.sv.natural_language_generation;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 12/23/14.
 */
public interface HasLexicalItems {
    public static Set<LexicalEntry> getEntries(){return new HashSet<>();};
}
