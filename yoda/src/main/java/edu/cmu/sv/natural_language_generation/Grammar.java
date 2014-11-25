package edu.cmu.sv.natural_language_generation;


import edu.cmu.sv.natural_language_generation.Templates.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/27/14.
 */
public class Grammar {

    public static GrammarPreferences DEFAULT_GRAMMAR_PREFERENCES = new Grammar.GrammarPreferences(.01, .2, 5, 2, 5, 5, 2, new HashMap<>());

    public static class GrammarPreferences{
        public double pExpandPrepositionalPhrase;
        public double pIncludeAdjective;
        public int maxUtteranceDepth;
        public int maxNounPhraseDepth;
        public int maxPhrasesPerPreposition;
        public int maxCombinations;
        public int maxWordForms;
        Map<Class<? extends Template>, Double> templateProbability;

        public GrammarPreferences(double pExpandPrepositionalPhrase, double pIncludeAdjective, int maxUtteranceDepth, int maxNounPhraseDepth, int maxPhrasesPerPreposition, int maxCombinations, int maxWordForms, Map<Class<? extends Template>, Double> templateProbability) {
            this.pExpandPrepositionalPhrase = pExpandPrepositionalPhrase;
            this.pIncludeAdjective = pIncludeAdjective;
            this.maxUtteranceDepth = maxUtteranceDepth;
            this.maxNounPhraseDepth = maxNounPhraseDepth;
            this.maxPhrasesPerPreposition = maxPhrasesPerPreposition;
            this.maxCombinations = maxCombinations;
            this.maxWordForms = maxWordForms;
            this.templateProbability = templateProbability;
        }
    }

    public static Set<Class<? extends Template>> grammar1_roots = new HashSet<>();
    public static Set<Class<? extends Template>> grammar1 = new HashSet<>();

    static {
        grammar1.add(SimpleNamedEntity0.class);
        grammar1.add(DefiniteReferenceTemplate0.class);
        grammar1.add(PPTemplate0.class);
        grammar1.add(AdjTemplate0.class);
        grammar1_roots.add(FragmentTemplate0.class);
        grammar1_roots.add(CommandTemplate0.class);
        grammar1_roots.add(HasPropertyYNQTemplate0.class);
        grammar1_roots.add(HasPropertyWHQTemplate0.class);
    }
}
