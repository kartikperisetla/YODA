package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.adjective.Cheap;
import edu.cmu.sv.ontology.adjective.Expensive;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.noun.Person;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.noun.Time;
import edu.cmu.sv.ontology.noun.poi_types.*;
import edu.cmu.sv.ontology.preposition.IsCloseTo;
import edu.cmu.sv.ontology.quality.unary_quality.Expensiveness;
import edu.cmu.sv.ontology.role.Agent;
import edu.cmu.sv.ontology.role.Destination;
import edu.cmu.sv.ontology.role.Origin;
import edu.cmu.sv.ontology.role.Patient;
import edu.cmu.sv.ontology.verb.GiveDirections;
import edu.cmu.sv.ontology.verb.HasProperty;
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
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "person");
            entry.add(LexicalEntry.PART_OF_SPEECH.S1_PRONOUN, "I");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "they");
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
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "give directions");
            entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "giving directions");
            Lexicon.add(GiveDirections.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "is");
            Lexicon.add(HasProperty.class, entry);
        }
    }

    //// Lexicon for adjectives
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "cheap");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "inexpensive");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "affordable");
            Lexicon.add(Cheap.class, entry);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "expensive");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "pricey");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "costly");
            Lexicon.add(Expensive.class, entry);
        }
    }

    //// Lexicon for prepositions
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "close to");
            entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "near to");
            entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "near");
            Lexicon.add(IsCloseTo.class, entry);
        }
    }

    //// Lexicon for transitive qualities
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "expensiveness");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "price range");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cost");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "affordability");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "prices");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "costs");
            Lexicon.add(Expensiveness.class, entry);
        }
    }

    //// Lexicon for roles
    static {
        {
            // directions X, directions to X, directions <...> to X
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "to");
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, "to");
            Lexicon.add(Destination.class, entry);
        }
        {
            // directions from X, directions <...> from X
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "from");
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, "from");
            Lexicon.add(Origin.class, entry);
        }
        {
            // directions from X, directions <...> from X
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_SUBJECT_PREFIX, "");
            Lexicon.add(Agent.class, entry);
        }
        {
            // directions from X, directions <...> from X
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
            Lexicon.add(Patient.class, entry);
        }

    }

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
                                             Grammar.GrammarPreferences grammarPreferences) throws NoLexiconEntryException {
        Set<String> ans = new HashSet<>();
        for (LexicalEntry lexicalEntry : Lexicon.get(cls)) {
            ans.addAll(lexicalEntry.get(partOfSpeech));
        }
        if (ans.size()==0)
            throw new NoLexiconEntryException();

        return Combination.randomSubset(ans, grammarPreferences.maxWordForms);
    }

    public static Set<String> getPOSForClassHierarchy(Class cls,
                                                      LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                                      Grammar.GrammarPreferences grammarPreferences) throws NoLexiconEntryException {
        if (! (Thing.class.isAssignableFrom(cls)))
            throw new NoLexiconEntryException();
        try {
            Set<String> ans = getPOSForClass((Class<? extends Thing>)cls, partOfSpeech, grammarPreferences);
            if (ans.size()==0){
                throw new NoLexiconEntryException();
            }
            return ans;
        } catch (NoLexiconEntryException e){
            return getPOSForClassHierarchy(cls.getSuperclass(), partOfSpeech, grammarPreferences);
        }
    }

    /**
     * LexicalEntry instances store a set of closely related words
     * used to describe a single concept from the ontology
     */
    public static class LexicalEntry {
        public enum PART_OF_SPEECH {
            WH_PRONOUN, S1_PRONOUN, S3_PRONOUN,
            SINGULAR_NOUN, PLURAL_NOUN,
            S1_VERB, S3_VERB, PRESENT_PROGRESSIVE_VERB,
            ADJECTIVE, RELATIONAL_PREPOSITIONAL_PHRASE,
        AS_SUBJECT_PREFIX, AS_OBJECT_PREFIX, AS_OBJECT2_PREFIX}
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


    static {

    }

    public static class NoLexiconEntryException extends Exception {}
}
