package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.utils.Combination;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 12/24/14.
 */
public class Lexicon {
    // Map from ontology concepts to sets of corresponding lexical entries
    private Map<Class<? extends Thing>, Set<LexicalEntry>> standardLexiconMap = new HashMap<>();
    private Map<Class<? extends Thing>, Set<LexicalEntry>> casualLexiconMap = new HashMap<>();


    /*
    * Extend this lexicon by adding all entries from otherLexicon
    * */
    public void loadLexicon(Lexicon otherLexicon){
        for (Class<? extends Thing> key : otherLexicon.standardLexiconMap.keySet()){
            for (LexicalEntry entry : otherLexicon.standardLexiconMap.get(key)){
                add(key, entry, false);
            }
        }
        for (Class<? extends Thing> key : otherLexicon.casualLexiconMap.keySet()){
            for (LexicalEntry entry : otherLexicon.casualLexiconMap.get(key)){
                add(key, entry, true);
            }
        }
    }


    public Set<LexicalEntry> get(Class<? extends Thing> cls, boolean allowCasual){
        Set<LexicalEntry> ans = new HashSet<>();
        if (standardLexiconMap.containsKey(cls))
            ans.addAll(standardLexiconMap.get(cls));
        if (allowCasual && casualLexiconMap.containsKey(cls))
            ans.addAll(casualLexiconMap.get(cls));
        return ans;
    }

    public void add(Class<? extends Thing> cls, LexicalEntry lexicalEntry, boolean isCasual){
        if (isCasual){
            if (!casualLexiconMap.containsKey(cls))
                casualLexiconMap.put(cls, new HashSet<>());
            casualLexiconMap.get(cls).add(lexicalEntry);

        } else {
            if (!standardLexiconMap.containsKey(cls))
                standardLexiconMap.put(cls, new HashSet<>());
            standardLexiconMap.get(cls).add(lexicalEntry);
        }

    }

    public Set<String> getPOSForClass(Class<? extends Thing> cls,
                                             LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                             boolean allowCasual) throws NoLexiconEntryException {
        Set<String> ans = new HashSet<>();
        for (LexicalEntry lexicalEntry : get(cls, allowCasual)) {
            ans.addAll(lexicalEntry.get(partOfSpeech));
        }
        if (ans.size()==0)
            throw new NoLexiconEntryException();

        return Combination.randomSubset(ans, 1);
    }

    public  Set<String> getPOSForClassHierarchy(Class cls,
                                                      LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                                      boolean allowCasual) throws NoLexiconEntryException {
        if (! (Thing.class.isAssignableFrom(cls)))
            throw new NoLexiconEntryException();
        try {
            Set<String> ans = getPOSForClass((Class<? extends Thing>)cls, partOfSpeech, allowCasual);
            if (ans.size()==0){
                throw new NoLexiconEntryException();
            }
            return ans;
        } catch (NoLexiconEntryException e){
            return getPOSForClassHierarchy(cls.getSuperclass(), partOfSpeech, allowCasual);
        }
    }

    /**
     * LexicalEntry instances store a set of closely related words
     * used to describe a single concept from the ontology
     */
    public static class LexicalEntry {
        public enum PART_OF_SPEECH {
            WH_PRONOUN, S3_PRONOUN,
            SINGULAR_NOUN, PLURAL_NOUN,
            S1_VERB,
            ADJECTIVE, PREPOSITION,
            AS_OBJECT_PREFIX, AS_OBJECT2_PREFIX}
        private Map<PART_OF_SPEECH, Set<String>> wordMap = new HashMap<>();

        public Set<String> get(PART_OF_SPEECH partOfSpeech){
            if (wordMap.containsKey(partOfSpeech))
                return wordMap.get(partOfSpeech);
            return new HashSet<>();
        }

        public void add(PART_OF_SPEECH partOfSpeech, String str){
            if (!wordMap.containsKey(partOfSpeech))
                wordMap.put(partOfSpeech, new HashSet<>());
            wordMap.get(partOfSpeech).add(str);
        }
    }

    public  class NoLexiconEntryException extends Exception {}
}
