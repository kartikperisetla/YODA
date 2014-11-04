package edu.cmu.sv.ontology.preposition;

import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.ontology.quality.binary_quality.HasDistance;
import edu.cmu.sv.ontology.quality.binary_quality.TransientPairwiseQuality;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/30/14.
 *
 * Relates two POI's that are geographically close to each other.
 */
public class IsCloseTo extends TransientPairwiseRole{
    @Override
    public double getCenter() {
        return 0;
    }

    @Override
    public double getSlope() {
        return 10;
    }

    static TransientPairwiseQuality myTransientQuality = new HasDistance();

    @Override
    public TransientPairwiseQuality getTransientPairwiseQuality() {
        return myTransientQuality;
    }

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
}
