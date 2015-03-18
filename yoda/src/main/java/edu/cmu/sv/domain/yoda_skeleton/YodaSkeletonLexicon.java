package edu.cmu.sv.domain.yoda_skeleton;

import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Agent;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Patient;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Person;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Time;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YodaSkeletonLexicon extends Lexicon {

    public YodaSkeletonLexicon() {
        //// Lexicon for high-level classes
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "what");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "it");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "that");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "this");
                add(Noun.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "who");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "person");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_PRONOUN, "I");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "they");
                add(Person.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "where");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "there");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "here");
                add(PointOfInterest.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "when");
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "what time");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "then");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "that time");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "this time");
                add(Time.class, entry, false);
            }
        }

        //// Entries for Roles
        {
            {
                // directions from X, directions <...> from X
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_SUBJECT_PREFIX, "");
                add(Agent.class, entry, false);
            }
            {
                // directions from X, directions <...> from X
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
                add(Patient.class, entry, false);
            }
        }
    }
}
