package edu.cmu.sv.domain.smart_house;

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
                add(SmartHouseOntologyRegistry.appliance, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "security system");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "security systems");
                add(SmartHouseOntologyRegistry.securitySystem, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "air conditioner");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "air conditioners");
                add(SmartHouseOntologyRegistry.airConditioner, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "room");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "rooms");
                add(SmartHouseOntologyRegistry.room, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "kitchen");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "kitchens");
                add(SmartHouseOntologyRegistry.kitchen, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "living room");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "living rooms");
                add(SmartHouseOntologyRegistry.livingRoom, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thermostat");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "thermostats");
                add(SmartHouseOntologyRegistry.thermostat, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "microwave");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "microwaves");
                add(SmartHouseOntologyRegistry.microwave, entry, false);
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
                add(SmartHouseOntologyRegistry.roomba, entry, false);
            }
        }

        //// Lexicon for prepositions
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "in");
                add(SmartHouseOntologyRegistry.isContainedBy, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "inside");
                entry.add(LexicalEntry.PART_OF_SPEECH.PREPOSITION, "at");
                add(SmartHouseOntologyRegistry.isContainedBy, entry, true);
            }
        }


            //// Lexicon for adjectives
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "on");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "turned on");
                add(SmartHouseOntologyRegistry.on, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "armed");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "active");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "activated");
                add(SmartHouseOntologyRegistry.on, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "off");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "turned off");
                add(SmartHouseOntologyRegistry.off, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "disarmed");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "inactive");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "deactivated");
                add(SmartHouseOntologyRegistry.off, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "clean");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "tidy");
                add(SmartHouseOntologyRegistry.clean, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "dirty");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "untidy");
                add(SmartHouseOntologyRegistry.dirty, entry, false);
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
                add(SmartHouseOntologyRegistry.turnOnAppliance, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "power up");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "arm");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "activate");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "active");
                add(SmartHouseOntologyRegistry.turnOnAppliance, entry, true);
            }

            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "clean");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "vacuum");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "clean up");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "clean");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vacuum");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "clean up");
                add(SmartHouseOntologyRegistry.cleanRoom, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "turn off");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "power off");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "turn off");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "power off");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "off");
                add(SmartHouseOntologyRegistry.turnOffAppliance, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "power down");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "disarm");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shut off");
                add(SmartHouseOntologyRegistry.turnOffAppliance, entry, true);
            }

        }

        //// Lexicon for roles
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
                add(SmartHouseOntologyRegistry.component, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
                add(SmartHouseOntologyRegistry.hasRoom, entry, false);
            }

        }

    }


}
