package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.smart_house.ontology.adjective.Off;
import edu.cmu.sv.domain.smart_house.ontology.adjective.On;
import edu.cmu.sv.domain.smart_house.ontology.noun.AirConditioner;
import edu.cmu.sv.domain.smart_house.ontology.noun.Room;
import edu.cmu.sv.domain.smart_house.ontology.noun.Person;
import edu.cmu.sv.domain.smart_house.ontology.noun.Thermostat;
import edu.cmu.sv.domain.smart_house.ontology.noun.Microwave;
import edu.cmu.sv.domain.smart_house.ontology.noun.SecuritySystem;
import edu.cmu.sv.domain.smart_house.ontology.role.Component;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOnAppliance;
import edu.cmu.sv.natural_language_generation.Lexicon;

/**
 * Created by David Cohen on 3/3/15.
 */
public class SmartHouseLexicon extends Lexicon {
    public SmartHouseLexicon() {
        //// Lexicon for nouns
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "security system");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "security systems");
                add(SecuritySystem.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "air conditioner");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "air conditioners");
                add(AirConditioner.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "room");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "rooms");
                add(Room.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "person");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "people");
                add(Person.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thermostat");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "thermostats");
                add(Thermostat.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "microwave");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "microwaves");
                add(Microwave.class, entry, false);
            }
        }

        //// Lexicon for adjectives
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "on");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "turned on");
                add(On.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "off");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "turned off");
                add(Off.class, entry, false);
            }
        }

        //// Lexicon for transitive qualities
        {
        }
        //// Lexicon for verbs
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "turn on");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "power on");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "turn on");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "power on");
                add(TurnOnAppliance.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "power up");
                add(TurnOnAppliance.class, entry, true);
            }
        }

        //// Lexicon for roles
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
                add(Component.class, entry, false);
            }

        }

    }


}
