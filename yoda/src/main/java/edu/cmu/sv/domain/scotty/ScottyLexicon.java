package edu.cmu.sv.domain.scotty;

import edu.cmu.sv.domain.scotty.ontology.nouns.poi_types.*;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Off;
import edu.cmu.sv.domain.smart_house.ontology.adjective.On;
import edu.cmu.sv.domain.smart_house.ontology.noun.*;
import edu.cmu.sv.domain.smart_house.ontology.role.Component;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOnAppliance;
import edu.cmu.sv.natural_language_generation.Lexicon;

/**
 * Created by David Cohen on 3/3/15.
 */
public class ScottyLexicon extends Lexicon {
    public ScottyLexicon() {
        //// Lexicon for points of interest

        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
            add(Restaurant.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bank");
            add(Bank.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bar");
            add(Bar.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bench");
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
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "coffee shop");
            add(Cafe.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food restaurant");
            add(FastFood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "garbage can");
            add(GarbageCan.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gas station");
            add(GasStation.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "graveyard");
            add(GraveYard.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hospital");
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
            add(MailBox.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "parking lot");
            add(Parking.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pharmacy");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "drug store");
            add(Pharmacy.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "place of worship");
            add(PlaceOfWorship.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "post office");
            add(PostOffice.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "public building");
            add(PublicBuilding.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "public telephone");
            add(PublicTelephone.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "recycling");
            add(Recycling.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "restaurants");
            add(Restaurant.class, entry, false);
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
            add(School.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shelter");
            add(Shelter.class, entry, false);
        }

    }


}
