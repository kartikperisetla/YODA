package edu.cmu.sv.natural_language_generation;


import edu.cmu.sv.natural_language_generation.internal_templates.*;
import edu.cmu.sv.natural_language_generation.nlg2_top_level_templates.AcceptTopLevelNLGTemplate;
import edu.cmu.sv.natural_language_generation.nlg2_top_level_templates.AcknowledgeTopLevelNLGTemplate;
import edu.cmu.sv.natural_language_generation.nlg2_top_level_templates.ConfirmGroundingSuggestionTopLevelNLGTemplate;
import edu.cmu.sv.natural_language_generation.nlg2_top_level_templates.DontKnowTopLevelNLGTemplate;
import edu.cmu.sv.natural_language_generation.top_level_templates.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/27/14.
 */
public class Grammar {

    public static GrammarPreferences DEFAULT_GRAMMAR_PREFERENCES = new Grammar.GrammarPreferences(.5, .01, .2, 3, 2, 3, 5, 2, new HashMap<>());
    public static GrammarPreferences CORPUS_GENERATION_PREFERENCES = new Grammar.GrammarPreferences(.9, .3, .2, 5, 3, 5, 5, 2, new HashMap<>());
    public static GrammarPreferences EXHAUSTIVE_GENERATION_PREFERENCES = new GrammarPreferences(.9, 1.0, 1.0, 5, 3, 3, 100, 100, new HashMap<>());

    public static class GrammarPreferences{
        public double referenceAmbiguityThreshold;
        public double pExpandPrepositionalPhrase;
        public double pIncludeAdjective;
        public int maxUtteranceDepth;
        public int maxNounPhraseDepth;
        public int maxPhrasesPerPreposition;
        public int maxCombinations;
        public int maxWordForms;
        Map<Class<? extends TopLevelNLGTemplate>, Double> templateProbability;

        public GrammarPreferences(double referenceAmbiguityThreshold, double pExpandPrepositionalPhrase, double pIncludeAdjective, int maxUtteranceDepth, int maxNounPhraseDepth, int maxPhrasesPerPreposition, int maxCombinations, int maxWordForms, Map<Class<? extends TopLevelNLGTemplate>, Double> templateProbability) {
            this.referenceAmbiguityThreshold = referenceAmbiguityThreshold;
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

    public static Set<Class<? extends TopLevelNLGTemplate>> grammar1_roots = new HashSet<>();
    public static Set<Class<? extends TopLevelNLGTemplate>> grammar1 = new HashSet<>();

    static {
        grammar1.add(SimpleNamedEntity0.class);
        grammar1.add(DefiniteReferenceTopLevelNLGTemplate0.class);
        grammar1.add(PPTopLevelNLGTemplate0.class);
        grammar1.add(AdjTopLevelNLGTemplate0.class);
        grammar1.add(NounClassTopLevelNLGTemplate.class);
        grammar1.add(ThingWithNameTopLevelNLGTemplate0.class);
        grammar1.add(IndefiniteDescriptionTopLevelNLGTemplate0.class);
        grammar1_roots.add(HasPropertyYNQTopLevelNLGTemplate0.class);
        grammar1_roots.add(HasPropertyWHQTopLevelNLGTemplate0.class);
        grammar1_roots.add(StatementTopLevelNLGTemplate.class);
        grammar1_roots.add(SearchReturnedNothingTopLevelNLGTemplate0.class);
        grammar1_roots.add(AcceptTopLevelNLGTemplate.class);
        grammar1_roots.add(RejectTopLevelNLGTemplate.class);
        grammar1_roots.add(AcknowledgeTopLevelNLGTemplate.class);
        grammar1_roots.add(OOCRespondToRequestSearchAlternativeTopLevelNLGTemplate.class);
        grammar1_roots.add(OOCRespondToRequestListOptionsTopLevelNLGTemplate.class);
        grammar1_roots.add(DontKnowTopLevelNLGTemplate.class);
        grammar1_roots.add(RequestConfirmValueTopLevelNLGTemplate0.class);
        grammar1_roots.add(ConfirmGroundingSuggestionTopLevelNLGTemplate.class);
        grammar1_roots.add(RequestAgentTopLevelNLGTemplate.class);
        grammar1_roots.add(RequestRoleTopLevelNLGTemplate.class);
//        grammar1_roots.add(RequestRoleGivenRoleTemplate.class);
        grammar1_roots.add(RequestFixMisunderstandingTopLevelNLGTemplate0.class);
        grammar1_roots.add(NotifyDialogLostTopLevelNLGTemplate0.class);
    }
}
