package edu.cmu.sv.domain.yoda_skeleton;

import edu.cmu.sv.natural_language_generation.Lexicon;

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
                add(YodaSkeletonOntologyRegistry.rootNoun, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "who");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "person");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "people");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "they");
                add(YodaSkeletonOntologyRegistry.person, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "where");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "there");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "here");
                add(YodaSkeletonOntologyRegistry.place, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "when");
                entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "what time");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "then");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "that time");
                entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "this time");
                add(YodaSkeletonOntologyRegistry.timeNounClass, entry, false);
            }
        }

        //// Entries for Roles
        {
            {
                // directions from X, directions <...> from X
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
                add(YodaSkeletonOntologyRegistry.patient, entry, false);
            }
            {
                // directions from X, directions <...> from X
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "at");
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, "at");
                add(YodaSkeletonOntologyRegistry.hasAtTime, entry, false);
            }
            {
                // directions from X, directions <...> from X
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "for");
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, "for");
                add(YodaSkeletonOntologyRegistry.hasAtTime, entry, true);
            }
        }
    }
}
