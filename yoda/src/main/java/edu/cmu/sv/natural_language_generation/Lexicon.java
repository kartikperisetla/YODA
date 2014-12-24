package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.noun.Person;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.noun.Time;
import edu.cmu.sv.ontology.noun.poi_types.*;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by David Cohen on 12/24/14.
 */
public class Lexicon {
    // Map from ontology concepts to sets of corresponding lexical entries
    private static Map<Class<? extends Thing>, Set<LexicalEntry>> lexiconMap = new HashMap<>();

    //// Lexicon for high-level classes
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "what");
            Lexicon.add(Noun.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "who");
            Lexicon.add(Person.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "where");
            Lexicon.add(PointOfInterest.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "when");
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "what time");
            Lexicon.add(Time.class, entry);
        }
    }

    //// Lexicon for points of interest
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bank");
            Lexicon.add(Bank.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bar");
            Lexicon.add(Bar.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bench");
            Lexicon.add(Bench.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bicycle parking");
            Lexicon.add(BicycleParking.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cafe");
            Lexicon.add(Cafe.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food restaurant");
            Lexicon.add(FastFood.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "garbage can");
            Lexicon.add(GarbageCan.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gas station");
            Lexicon.add(GasStation.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "graveyard");
            Lexicon.add(GraveYard.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hospital");
            Lexicon.add(Hospital.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "kindergarten");
            Lexicon.add(Kindergarten.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mail box");
            Lexicon.add(MailBox.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "parking lot");
            Lexicon.add(Parking.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pharmacy");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "drug store");
            Lexicon.add(Pharmacy.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "place of worship");
            Lexicon.add(PlaceOfWorship.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "post office");
            Lexicon.add(PostOffice.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "public building");
            Lexicon.add(PublicBuilding.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "public telephone");
            Lexicon.add(PublicTelephone.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "recycling");
            Lexicon.add(Recycling.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
            Lexicon.add(Restaurant.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restroom");
            Lexicon.add(Restroom.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "school");
            Lexicon.add(School.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shelter");
            Lexicon.add(Shelter.class, entry);
        }
    }

    //// Lexicon for verbs

    //// Lexicon for adjectives

    //// Lexicon for prepositions

    public static Set<LexicalEntry> get(Class<? extends Thing> cls){
        if (lexiconMap.containsKey(cls))
            return lexiconMap.get(cls);
        return new HashSet<>();
    }

    public static void add(Class<? extends Thing> cls, LexicalEntry lexicalEntry){
        if (!lexiconMap.containsKey(cls))
            lexiconMap.put(cls, new HashSet<>());
        lexiconMap.get(cls).add(lexicalEntry);
    }

    public static Set<String> getPOSForClass(Class<? extends Thing> cls,
                                             LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                             YodaEnvironment yodaEnvironment) throws NoLexiconEntryException {
        Set<String> ans = new HashSet<>();
        for (LexicalEntry lexicalEntry : Lexicon.get(cls)) {
            ans.addAll(lexicalEntry.get(partOfSpeech));
        }
        if (ans.size()==0)
            throw new NoLexiconEntryException();
        if (ans.size()<=yodaEnvironment.nlg.grammarPreferences.maxWordForms)
            return ans;
        return new HashSet<>(Arrays.asList(
                Arrays.copyOf(
                        NaturalLanguageGenerator.randomData.nextSample(ans, yodaEnvironment.nlg.grammarPreferences.maxWordForms),
                        yodaEnvironment.nlg.grammarPreferences.maxWordForms, String[].class)));
    }

    public static Set<String> getPOSForClassHierarchy(Class cls,
                                                      LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                                      YodaEnvironment yodaEnvironment) throws NoLexiconEntryException {
        if (! (Thing.class.isAssignableFrom(cls)))
            throw new NoLexiconEntryException();
        try {
            Set<String> ans = getPOSForClass((Class<? extends Thing>)cls, partOfSpeech, yodaEnvironment);
            if (ans.size()==0){
                throw new NoLexiconEntryException();
            }
            return ans;
        } catch (NoLexiconEntryException e){
            return getPOSForClassHierarchy(cls.getSuperclass(), partOfSpeech, yodaEnvironment);
        }
    }

    /**
     * LexicalEntry instances store a set of closely related words
     * used to describe a single concept from the ontology
     */
    public static class LexicalEntry {
        public enum PART_OF_SPEECH {WH_PRONOUN, SINGULAR_NOUN, PRESENT_SINGULAR_VERB, ADJECTIVE, RELATIONAL_PREPOSITIONAL_PHRASE}
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

        // verbs
        public Set<String> presentSingularVerbs = new HashSet<>();

        // adjectives
        public Set<String> adjectives = new HashSet<>();

        // prepositional phrases
        public Set<String> relationalPrepositionalPhrases = new HashSet<>();

    }


    static {

    }

    public static class NoLexiconEntryException extends Exception {}
}
