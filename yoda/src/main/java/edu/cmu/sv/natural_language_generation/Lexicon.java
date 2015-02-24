package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.adjective.Cheap;
import edu.cmu.sv.ontology.adjective.Expensive;
import edu.cmu.sv.ontology.adjective.Good;
import edu.cmu.sv.ontology.adjective.Popular;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.noun.Person;
import edu.cmu.sv.ontology.noun.PointOfInterest;
import edu.cmu.sv.ontology.noun.Time;
import edu.cmu.sv.ontology.noun.poi_types.*;
import edu.cmu.sv.ontology.preposition.IsCloseTo;
import edu.cmu.sv.ontology.quality.unary_quality.Expensiveness;
import edu.cmu.sv.ontology.quality.unary_quality.Goodness;
import edu.cmu.sv.ontology.role.Agent;
import edu.cmu.sv.ontology.role.Destination;
import edu.cmu.sv.ontology.role.Origin;
import edu.cmu.sv.ontology.role.Patient;
import edu.cmu.sv.ontology.verb.GiveDirections;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.verb.MakeReservation;
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
    private static Map<Class<? extends Thing>, Set<LexicalEntry>> standardLexiconMap = new HashMap<>();
    private static Map<Class<? extends Thing>, Set<LexicalEntry>> casualLexiconMap = new HashMap<>();

    //// Lexicon for high-level classes
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "what");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "it");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "that");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "this");
            Lexicon.add(Noun.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "who");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "person");
            entry.add(LexicalEntry.PART_OF_SPEECH.S1_PRONOUN, "I");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "they");
            Lexicon.add(Person.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "where");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "there");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "here");
            Lexicon.add(PointOfInterest.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "when");
            entry.add(LexicalEntry.PART_OF_SPEECH.WH_PRONOUN, "what time");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "then");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "that time");
            entry.add(LexicalEntry.PART_OF_SPEECH.S3_PRONOUN, "this time");
            Lexicon.add(Time.class, entry, false);
        }
    }

    //// Lexicon for points of interest
    static {


        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "restaurants");
            Lexicon.add(Restaurants.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "some food");
            Lexicon.add(Food.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bars");
            Lexicon.add(Bars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mexican restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mexican places");
            Lexicon.add(Mexican.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican food");
            Lexicon.add(Mexican.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "american restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "american restaurants");
            Lexicon.add(AmericanTraditional.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "american food");
            Lexicon.add(AmericanTraditional.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "fast food restaurants");
            Lexicon.add(FastFood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food");
            Lexicon.add(FastFood.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pizza");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pizza restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pizza restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pizzeria");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pizzerias");
            Lexicon.add(Pizza.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "travel agency");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "kiosk");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "travel guide");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "travel agencies");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "kiosks");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "travel guides");
            Lexicon.add(HotelsAndTravel.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich store");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sandwich shops");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sandwich stores");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sandwich restaurants");
            Lexicon.add(Sandwiches.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "coffee shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "coffee shops");
            Lexicon.add(CoffeeAndTea.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern american restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "modern american restaurants");
            Lexicon.add(AmericanNew.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian restaurants");
            Lexicon.add(Italian.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "italian places");
            Lexicon.add(Italian.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "chinese restaurants");
            Lexicon.add(Chinese.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "chinese places");
            Lexicon.add(Chinese.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hotel");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "motel");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "inn");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hotels");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "motels");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "inns");
            Lexicon.add(Hotels.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hamburger restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hamburger restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hamburger stand");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hamburger stands");
            Lexicon.add(Burgers.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "burger restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "burger restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "burger joint");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hamburger joint");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "burger joints");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hamburger joints");
            Lexicon.add(Burgers.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "grocer");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "grocers");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "grocery store");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "grocery stores");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "supermarket");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "supermarkets");
            Lexicon.add(Grocery.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "breakfast restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "breakfast restaurants");
            Lexicon.add(BreakfastAndBrunch.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "breakfast food");
            Lexicon.add(BreakfastAndBrunch.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "creamery");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "creameries");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ice cream restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "ice cream restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ice cream store");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "ice cream stores");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ice cream shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "ice cream shops");
            Lexicon.add(IceCreamAndFrozenYogurt.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "specialty restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "specialty restaurants");
            Lexicon.add(SpecialtyFood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bakery");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bakeries");
            Lexicon.add(Bakeries.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pub");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pubs");
            Lexicon.add(Pubs.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "japanese restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "japanese restaurants");
            Lexicon.add(Japanese.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sports bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sports bars");
            Lexicon.add(SportsBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "convenience store");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "convenience stores");
            Lexicon.add(ConvenienceStores.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "deli");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "delis");
            Lexicon.add(Delis.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sushi bars");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sushi restaurants");
            Lexicon.add(SushiBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sushi places");
            Lexicon.add(SushiBars.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "steak house");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "steakhouse");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "steak houses");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "steakhouses");
            Lexicon.add(Steakhouses.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cafe");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cafes");
            Lexicon.add(Cafes.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "seafood restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "seafood restaurants");
            Lexicon.add(Seafood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "seafood place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "seafood places");
            Lexicon.add(Seafood.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dessert shops");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dessert restaurants");
            Lexicon.add(Desserts.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dessert places");
            Lexicon.add(Desserts.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "buffet");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "buffets");
            Lexicon.add(Buffets.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "barbeque");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "barbeques");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bbq");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bbqs");
            Lexicon.add(Barbeque.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "thai restaurants");
            Lexicon.add(Thai.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "thai places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai food");
            Lexicon.add(Thai.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mediterranean restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mediterranean restaurants");
            Lexicon.add(Mediterranean.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mediterranean place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mediterranean places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mediterranean food");
            Lexicon.add(Mediterranean.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "spirits");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wine and spirits");
            Lexicon.add(BeerWineAndSpirits.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wings");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wings place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wing stop");
            Lexicon.add(ChickenWings.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "asian fusion restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "asian fusion restaurants");
            Lexicon.add(AsianFusion.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "asian fusion place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "asian fusion places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "asian fusion food");
            Lexicon.add(AsianFusion.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "smoothie shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "smoothie shops");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "juice bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "juice bars");
            Lexicon.add(JuiceBarsAndSmoothies.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "greek restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "greek places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek food");
            Lexicon.add(Greek.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "indian restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "indian places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian food");
            Lexicon.add(Indian.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tex mex restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tex mex places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex food");
            Lexicon.add(TexMex.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "donut shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "donut shops");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "donut store");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "donut stores");
            Lexicon.add(Donuts.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "diner");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "diners");
            Lexicon.add(Diners.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog stand");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hot dog stands");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hot dog restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hot dog places");
            Lexicon.add(HotDogs.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vietnamese restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vietnamese places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese food");
            Lexicon.add(Vietnamese.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wine bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "wine bars");
            Lexicon.add(WineBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "local restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "local places");
            Lexicon.add(LocalFlavor.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "salad restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "salad places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad food");
            Lexicon.add(Salad.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dive bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dive bars");
            Lexicon.add(DiveBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegetarian restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegetarian places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian food");
            Lexicon.add(Vegetarian.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "british restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "british places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british food");
            Lexicon.add(British.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "french restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "french restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "french food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "french place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "french places");
            Lexicon.add(French.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bagel restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bagel restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bagels");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bagel");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bagel place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bagel places");
            Lexicon.add(Bagels.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "korean restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "korean restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "korean food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "korean place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "korean places");
            Lexicon.add(Korean.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ethnic restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "ethnic restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ethnic food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ethnic place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "ethnic places");
            Lexicon.add(EthnicFood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hawaiian restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hawaiian restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hawaiian food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hawaiian place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hawaiian places");
            Lexicon.add(Hawaiian.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "catering");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "caterer");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "caterers");
            Lexicon.add(Caterers.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gluten free restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gluten free restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gluten free food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gluten free place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gluten free places");
            Lexicon.add(GlutenFree.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "middle eastern restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "middle eastern restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "middle eastern food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "middle eastern place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "middle eastern places");
            Lexicon.add(MiddleEastern.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "farmers market");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "farmers markets");
            Lexicon.add(FarmersMarket.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gastro pub");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gastro pubs");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gastropub");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gastropubs");
            Lexicon.add(Gastropubs.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "latin american restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "latin american restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "latin american food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "latin american place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "latin american places");
            Lexicon.add(LatinAmerican.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "food truck");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "food trucks");
            Lexicon.add(FoodTrucks.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "karaoke place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "karaoke");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "karaoke places");
            Lexicon.add(Karaoke.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "candy store");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "candy stores");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "candy restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "candy restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "candy food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "candy place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "candy places");
            Lexicon.add(CandyStores.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "brewery");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "breweries");
            Lexicon.add(Breweries.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fish and chips");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fish n' chips");
            Lexicon.add(FishAndChips.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegan restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegan restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegan food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegan place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegan places");
            Lexicon.add(Vegan.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gay bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gay bars");
            Lexicon.add(GayBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chocolate restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "chocolate restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chocolate food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chocolate place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "chocolate places");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "chocolatiers");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chocolatier");
            Lexicon.add(ChocolatiersAndShops.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "food delivery service");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "food delivery");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "food delivery services");
            Lexicon.add(FoodDeliveryServices.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pakistani restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pakistani restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pakistani food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pakistani place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pakistani places");
            Lexicon.add(Pakistani.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shaved ice restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "shaved ice restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shaved ice");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shaved ice place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "shaved ice places");
            Lexicon.add(ShavedIce.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "food stand");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "food stands");
            Lexicon.add(FoodStands.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "filipino restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "filipino restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "filipino food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "filipino place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "filipino places");
            Lexicon.add(Filipino.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cocktail bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cocktail bars");
            Lexicon.add(CocktailBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "southern restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "southern restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "southern food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "southern place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "southern places");
            Lexicon.add(Southern.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hookah bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hookah bars");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hookah place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hookah places");
            Lexicon.add(HookahBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cajun restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cajun restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cajun food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cajun place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cajun places");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "creole restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "creole restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "creole food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "creole place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "creole places");
            Lexicon.add(CajunCreole.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "irish restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "irish restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "irish food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "irish place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "irish places");
            Lexicon.add(Irish.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tea room");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tea rooms");
            Lexicon.add(TeaRooms.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soul food restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "soul food restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soul food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soul food place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "soul food places");
            Lexicon.add(SoulFood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soup restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "soup restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soup");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soup place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "soup places");
            Lexicon.add(Soup.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "caribbean restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "caribbean restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "caribbean food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "caribbean place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "caribbean places");
            Lexicon.add(Caribbean.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "spanish restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "spanish restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "spanish food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "spanish place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "spanish places");
            Lexicon.add(Spanish.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tapas restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tapas restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tapas");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tapas place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tapas places");
            Lexicon.add(TapasSmallPlates.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fruit");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "fruits");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "veggies");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegetables");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetable");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "veggie");
            Lexicon.add(FruitsAndVeggies.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheesesteak restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cheesesteak restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheesesteak");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cheesesteaks");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheesesteak place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cheesesteak places");
            Lexicon.add(Cheesesteaks.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tapas bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tapas bars");
            Lexicon.add(TapasBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sports club");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sports clubs");
            Lexicon.add(SportsClubs.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dim sum restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dim sum restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dim sum");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dim sum place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dim sum places");
            Lexicon.add(DimSum.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "comfort food");
            Lexicon.add(ComfortFood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern european restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "modern european restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern european food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern european place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "modern european places");
            Lexicon.add(ModernEuropean.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "scottish restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "scottish restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "scottish food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "scottish place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "scottish places");
            Lexicon.add(Scottish.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "crepe restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "crepe restaurants");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "creperie");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "creperies");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "crepe place");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "crepe places");
            Lexicon.add(Creperies.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheese shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheese shops");
            Lexicon.add(CheeseShops.class, entry, false);
        }

    }

    //// Lexicon for verbs
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "make a reservation");
            entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "making a reservation");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "reservation");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "reservations");
            Lexicon.add(MakeReservation.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "make reservation");
            entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "making reservation");
            Lexicon.add(MakeReservation.class, entry, true);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "give directions");
            entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "giving directions");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "direction");
            entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "directions");
            Lexicon.add(GiveDirections.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "is");
            Lexicon.add(HasProperty.class, entry, false);
        }
    }

    //// Lexicon for adjectives
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "cheap");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "inexpensive");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "affordable");
            Lexicon.add(Cheap.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "expensive");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "pricey");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "costly");
            Lexicon.add(Expensive.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "good");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "excellent");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "highly rated");
            Lexicon.add(Good.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "en vogue");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "hip");
            entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "popular");
            Lexicon.add(Popular.class, entry, false);
        }
    }

    //// Lexicon for prepositions
    static {
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "close to");
            entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "near to");
            entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "near");
            entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "by");
            Lexicon.add(IsCloseTo.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "on");
            Lexicon.add(IsCloseTo.class, entry, true);
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
            Lexicon.add(Expensiveness.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "quality");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "rating");
            Lexicon.add(Goodness.class, entry, false);
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
            Lexicon.add(Destination.class, entry, false);
        }
        {
            // directions from X, directions <...> from X
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "from");
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT2_PREFIX, "from");
            Lexicon.add(Origin.class, entry, false);
        }
        {
            // directions from X, directions <...> from X
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_SUBJECT_PREFIX, "");
            Lexicon.add(Agent.class, entry, false);
        }
        {
            // directions from X, directions <...> from X
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.AS_OBJECT_PREFIX, "");
            Lexicon.add(Patient.class, entry, false);
        }

    }

    public static Set<LexicalEntry> get(Class<? extends Thing> cls, boolean allowCasual){
        Set<LexicalEntry> ans = new HashSet<>();
        if (standardLexiconMap.containsKey(cls))
            ans.addAll(standardLexiconMap.get(cls));
        if (allowCasual && casualLexiconMap.containsKey(cls))
            ans.addAll(casualLexiconMap.get(cls));
        return ans;
    }

    public static void add(Class<? extends Thing> cls, LexicalEntry lexicalEntry, boolean isCasual){
        if (isCasual){
            if (!casualLexiconMap.containsKey(cls))
                casualLexiconMap.put(cls, new HashSet<>());
            casualLexiconMap.get(cls).add(lexicalEntry);

        } else {
            if (!standardLexiconMap.containsKey(cls))
                standardLexiconMap.put(cls, new HashSet<>());
            standardLexiconMap.get(cls).add(lexicalEntry);
        }

    }

    public static Set<String> getPOSForClass(Class<? extends Thing> cls,
                                             LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                             Grammar.GrammarPreferences grammarPreferences,
                                             boolean allowCasual) throws NoLexiconEntryException {
        Set<String> ans = new HashSet<>();
        for (LexicalEntry lexicalEntry : Lexicon.get(cls, allowCasual)) {
            ans.addAll(lexicalEntry.get(partOfSpeech));
        }
        if (ans.size()==0)
            throw new NoLexiconEntryException();

        return Combination.randomSubset(ans, grammarPreferences.maxWordForms);
    }

    public static Set<String> getPOSForClassHierarchy(Class cls,
                                                      LexicalEntry.PART_OF_SPEECH partOfSpeech,
                                                      Grammar.GrammarPreferences grammarPreferences,
                                                      boolean allowCasual) throws NoLexiconEntryException {
        if (! (Thing.class.isAssignableFrom(cls)))
            throw new NoLexiconEntryException();
        try {
            Set<String> ans = getPOSForClass((Class<? extends Thing>)cls, partOfSpeech, grammarPreferences, allowCasual);
            if (ans.size()==0){
                throw new NoLexiconEntryException();
            }
            return ans;
        } catch (NoLexiconEntryException e){
            return getPOSForClassHierarchy(cls.getSuperclass(), partOfSpeech, grammarPreferences, allowCasual);
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
