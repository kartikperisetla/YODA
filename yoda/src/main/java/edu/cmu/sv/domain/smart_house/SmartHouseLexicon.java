package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.smart_house.ontology.adjective.Clean;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Dirty;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Hot;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Cold;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Off;
import edu.cmu.sv.domain.smart_house.ontology.adjective.On;
import edu.cmu.sv.domain.smart_house.ontology.noun.*;
import edu.cmu.sv.domain.smart_house.ontology.preposition.IsContainedBy;
import edu.cmu.sv.domain.smart_house.ontology.role.Component;
import edu.cmu.sv.domain.smart_house.ontology.role.HasRoom;
import edu.cmu.sv.domain.smart_house.ontology.verb.CleanRoom;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOffAppliance;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOnAppliance;
import edu.cmu.sv.domain.smart_house.ontology.verb.IncreaseTemperature;
import edu.cmu.sv.domain.smart_house.ontology.verb.DecreaseTemperature;
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
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "appliance");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "appliances");
                add(Appliance.class, entry, false);
            }
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
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "kitchen");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "kitchens");
                add(Kitchen.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "living room");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "living rooms");
                add(LivingRoom.class, entry, false);
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
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "roomba");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vacuum robot");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "robot");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vacuum cleaner robot");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vacuum cleaner robots");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vacuum robots");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "robots");
                add(Roomba.class, entry, false);
            }
        }

        //// Lexicon for prepositions
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "in");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "inside");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "at");
                add(IsContainedBy.class, entry, false);
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
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "clean");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "tidy");
                add(Clean.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "hot");
                add(Hot.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "cold");
                add(Cold.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "dirty");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "untidy");
                add(Dirty.class, entry, false);
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
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "on");
                add(TurnOnAppliance.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "power up");
                add(TurnOnAppliance.class, entry, true);
            }


            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "clean");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "vacuum");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "clean up");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "clean");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vacuum");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "clean up");
                add(CleanRoom.class, entry, false);
            }

            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "warm up");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "heat up");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "warm up");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "heat up");
                add(IncreaseTemperature.class, entry, false);
            }

            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "cool down");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cool down");
                add(DecreaseTemperature.class, entry, false);
            }

            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "turn off");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "power off");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "turn off");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "power off");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "off");
                add(TurnOffAppliance.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "power down");
                add(TurnOffAppliance.class, entry, true);
            }

        }

        //// Lexicon for roles
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
                add(Component.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
                add(HasRoom.class, entry, false);
            }

        }

    }


}
