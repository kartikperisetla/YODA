package edu.cmu.sv.domain.scotty;

import edu.cmu.sv.domain.scotty.ontology.nouns.poi_types.*;
import edu.cmu.sv.domain.yelp_phoenix.ontology.preposition.IsCloseTo;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality.Expensiveness;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality.Goodness;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.Destination;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.Origin;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.GiveDirections;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.natural_language_generation.Lexicon;

/**
 * Created by David Cohen on 3/3/15.
 */
public class ScottyLexicon extends Lexicon {
    public ScottyLexicon() {
        //// Lexicon for points of interest
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurants");
                add(Restaurant.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bank");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "banks");
                add(Bank.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bars");
                add(Bar.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bench");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "benches");
                add(Bench.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bicycle parking");
                add(BicycleParking.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cafe");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cafes");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "coffee shop");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "coffee shops");
                add(Cafe.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "fast food restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "fast food places");
                add(FastFood.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food joint");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "fast food joints");
                add(FastFood.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "garbage can");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "garbage cans");
                add(GarbageCan.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gas station");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gas stations");
                add(GasStation.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "graveyard");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "graveyards");
                add(GraveYard.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hospital");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hospitals");
                add(Hospital.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "kindergarten");
                add(Kindergarten.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mail box");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mail boxes");
                add(MailBox.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "parking lot");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "parking spot");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "parking lots");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "parking spots");
                add(Parking.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pharmacy");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pharmacies");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "drug store");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "drug stores");
                add(Pharmacy.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "place of worship");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "places of worship");
                add(PlaceOfWorship.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "post office");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "post offices");
                add(PostOffice.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "public building");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "public buildings");
                add(PublicBuilding.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "public telephone");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "public telephones");
                add(PublicTelephone.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "recycling");
                add(Recycling.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restroom");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "restrooms");
                add(Restroom.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "school");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "schools");
                add(School.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shelter");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "shelters");
                add(Shelter.class, entry, false);
            }
        }


        //// Lexicon for prepositions
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "close to");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "near to");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "near");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "by");
                add(IsCloseTo.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "on");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "closest to");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "closest");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "nearest to");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "nearest");
                add(IsCloseTo.class, entry, true);
            }
        }

        //// Lexicon for transitive qualities
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "expensiveness");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "price range");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cost");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "affordability");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "prices");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "costs");
                add(Expensiveness.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "quality");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "rating");
                add(Goodness.class, entry, false);
            }
        }
        //// Lexicon for verbs
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "give directions");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "direction");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "directions");
                add(GiveDirections.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "is");
                add(HasProperty.class, entry, false);
            }
        }

        //// Lexicon for roles
        {
            {
                // directions X, directions to X, directions <...> to X
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "to");
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, "to");
                add(Destination.class, entry, false);
            }
            {
                // directions X, directions to X, directions <...> to X
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "at");
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, "at");
                add(Destination.class, entry, true);
            }
            {
                // directions from X, directions <...> from X
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "from");
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, "from");
                add(Origin.class, entry, false);
            }

        }
    }


}
