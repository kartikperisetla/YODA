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
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
            Lexicon.add(Restaurants.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "some food");
            Lexicon.add(Food.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bar");
            Lexicon.add(Bars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican food");
            Lexicon.add(Mexican.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "american restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "american food");
            Lexicon.add(AmericanTraditional.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food");
            Lexicon.add(FastFood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pizza");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pizzeria");
            Lexicon.add(Pizza.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "travel agency");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "kiosk");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "travel guide");
            Lexicon.add(HotelsAndTravel.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich store");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich restaurant");
            Lexicon.add(Sandwiches.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "coffee shop");
            Lexicon.add(CoffeeAndTea.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern american restaurant");
            Lexicon.add(AmericanNew.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian restaurant");
            Lexicon.add(Italian.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese restaurant");
            Lexicon.add(Chinese.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hotel");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "motel");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "inn");
            Lexicon.add(Hotels.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "burger joint");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hamburger joint");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "burger restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hamburger restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hambuger stand");
            Lexicon.add(Burgers.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "grocer");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "grocery store");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "supermarket");
            Lexicon.add(Grocery.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "breakfast food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "breakfast restaurant");
            Lexicon.add(BreakfastAndBrunch.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "creamery");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ice cream restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ice cream store");
            Lexicon.add(IceCreamAndFrozenYogurt.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "specialty restaurant");
            Lexicon.add(SpecialtyFood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bakery");
            Lexicon.add(Bakeries.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pub");
            Lexicon.add(Pubs.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "japanese restaurant");
            Lexicon.add(Japanese.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sports bar");
            Lexicon.add(SportsBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "convenience store");
            Lexicon.add(ConvenienceStores.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "deli");
            Lexicon.add(Delis.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi bar");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi place");
            Lexicon.add(SushiBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "steak house");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "steakhouse");
            Lexicon.add(Steakhouses.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cafe");
            Lexicon.add(Cafes.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "seafood restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "seafood place");
            Lexicon.add(Seafood.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert place");
            Lexicon.add(Desserts.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "buffet");
            Lexicon.add(Buffets.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "barbeque");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bbq");
            Lexicon.add(Barbeque.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai food");
            Lexicon.add(Thai.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mediterranean restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mediterranean place");
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
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "asian fusion place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "asian fusion food");
            Lexicon.add(AsianFusion.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "smoothie shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "juice bar");
            Lexicon.add(JuiceBarsAndSmoothies.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek food");
            Lexicon.add(Greek.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian food");
            Lexicon.add(Indian.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex food");
            Lexicon.add(TexMex.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "donut shop");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "donut store");
            Lexicon.add(Donuts.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "diner");
            Lexicon.add(Diners.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog stand");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog place");
            Lexicon.add(HotDogs.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese food");
            Lexicon.add(Vietnamese.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wine bar");
            Lexicon.add(WineBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local food");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local place");
            Lexicon.add(LocalFlavor.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad food");
            Lexicon.add(Salad.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dive bar");
            Lexicon.add(DiveBars.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian food");
            Lexicon.add(Vegetarian.class, entry, false);
        }
        {
            LexicalEntry entry = new LexicalEntry();
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british restaurant");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british place");
            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british food");
            Lexicon.add(British.class, entry, false);
        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(French.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Bagels.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Korean.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(EthnicFood.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Hawaiian.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Caterers.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(GlutenFree.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(MiddleEastern.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(FarmersMarket.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Gastropubs.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(LatinAmerican.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(FoodTrucks.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Karaoke.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(CandyStores.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Breweries.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(FishAndChips.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Vegan.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(GayBars.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(ChocolatiersAndShops.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(FoodDeliveryServices.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Pakistani.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(ShavedIce.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(FoodStands.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Filipino.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(CocktailBars.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Southern.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(HookahBars.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(CajunCreole.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Irish.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(TeaRooms.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(SoulFood.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Soup.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Caribbean.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Spanish.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(TapasSmallPlates.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(FruitsAndVeggies.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Cheesesteaks.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(TapasBars.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(SportsClubs.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(DimSum.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(ComfortFood.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(ModernEuropean.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Scottish.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(Creperies.class, entry, false);
//        }
//        {
//            LexicalEntry entry = new LexicalEntry();
//            entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "");
//            Lexicon.add(CheeseShops.class, entry, false);
//        }

        


        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
        //     Lexicon.add(Restaurant.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bank");
        //     Lexicon.add(Bank.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bar");
        //     Lexicon.add(Bar.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bench");
        //     Lexicon.add(Bench.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bicycle parking");
        //     Lexicon.add(BicycleParking.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cafe");
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "coffee shop");
        //     Lexicon.add(Cafe.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food restaurant");
        //     Lexicon.add(FastFood.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "garbage can");
        //     Lexicon.add(GarbageCan.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gas station");
        //     Lexicon.add(GasStation.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "graveyard");
        //     Lexicon.add(GraveYard.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hospital");
        //     Lexicon.add(Hospital.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "kindergarten");
        //     Lexicon.add(Kindergarten.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mail box");
        //     Lexicon.add(MailBox.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "parking lot");
        //     Lexicon.add(Parking.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pharmacy");
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "drug store");
        //     Lexicon.add(Pharmacy.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "place of worship");
        //     Lexicon.add(PlaceOfWorship.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "post office");
        //     Lexicon.add(PostOffice.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "public building");
        //     Lexicon.add(PublicBuilding.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "public telephone");
        //     Lexicon.add(PublicTelephone.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "recycling");
        //     Lexicon.add(Recycling.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
        //     Lexicon.add(Restaurant.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restroom");
        //     Lexicon.add(Restroom.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "school");
        //     Lexicon.add(School.class, entry, false);
        // }
        // {
        //     LexicalEntry entry = new LexicalEntry();
        //     entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shelter");
        //     Lexicon.add(Shelter.class, entry, false);
        // }
    }

    //// Lexicon for verbs
    static {
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
