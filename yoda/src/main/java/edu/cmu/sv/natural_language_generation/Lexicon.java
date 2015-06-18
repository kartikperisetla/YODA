package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.domain.ontology2.Noun2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 12/24/14.
 */
public class Lexicon {
    // Map from ontology concepts to sets of corresponding lexical entries
    private Map<Object, Set<LexicalEntry>> standardLexiconMap = new HashMap<>();
    private Map<Object, Set<LexicalEntry>> understandingOnlyLexiconMap = new HashMap<>();


    /*
    * Extend this lexicon by adding all entries from otherLexicon
    * */
    public void loadLexicon(Lexicon otherLexicon){
        for (Object key : otherLexicon.standardLexiconMap.keySet()){
            for (LexicalEntry entry : otherLexicon.standardLexiconMap.get(key)){
                add(key, entry, false);
            }
        }
        for (Object key : otherLexicon.understandingOnlyLexiconMap.keySet()){
            for (LexicalEntry entry : otherLexicon.understandingOnlyLexiconMap.get(key)){
                add(key, entry, true);
            }
        }
    }


    public Set<LexicalEntry> get(Object cls, boolean allowUnderstandingOnly){
        Set<LexicalEntry> ans = new HashSet<>();
        if (standardLexiconMap.containsKey(cls))
            ans.addAll(standardLexiconMap.get(cls));
        if (allowUnderstandingOnly && understandingOnlyLexiconMap.containsKey(cls))
            ans.addAll(understandingOnlyLexiconMap.get(cls));
        return ans;
    }

    public void add(Object cls, LexicalEntry lexicalEntry, boolean understandingOnly){
        if (understandingOnly){
            if (!understandingOnlyLexiconMap.containsKey(cls))
                understandingOnlyLexiconMap.put(cls, new HashSet<>());
            understandingOnlyLexiconMap.get(cls).add(lexicalEntry);

        } else {
            if (!standardLexiconMap.containsKey(cls))
                standardLexiconMap.put(cls, new HashSet<>());
            standardLexiconMap.get(cls).add(lexicalEntry);
        }

    }

    public Set<String> getPOSForClass(Object cls,
                                             LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                             boolean allowUnderstandingOnly) throws NoLexiconEntryException {
        Set<String> ans = new HashSet<>();
        for (LexicalEntry lexicalEntry : get(cls, allowUnderstandingOnly)) {
            ans.addAll(lexicalEntry.get(partOfSpeech));
        }
        if (ans.size()==0)
            throw new NoLexiconEntryException();
        return ans;
    }

    public  Set<String> getPOSForClassHierarchy(Noun2 cls,
                                                LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                                boolean allowUnderstandingOnly) throws NoLexiconEntryException {
        try {
            Set<String> ans = getPOSForClass((Object)cls, partOfSpeech, allowUnderstandingOnly);
            if (ans.size()==0){
                throw new NoLexiconEntryException();
            }
            return ans;
        } catch (NoLexiconEntryException e){
            if (cls.directParent!=null)
                return getPOSForClassHierarchy(cls.directParent, partOfSpeech, allowUnderstandingOnly);
            return new HashSet<>();
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
