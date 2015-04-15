package edu.cmu.sv.domain.yelp_phoenix;

import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Cheap;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Expensive;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Good;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Popular;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.poi_types.*;
import edu.cmu.sv.domain.yelp_phoenix.ontology.preposition.IsCloseTo;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality.Expensiveness;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality.Goodness;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.Destination;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.Origin;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.GiveDirections;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.MakeReservation;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.natural_language_generation.Lexicon;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YelpPhoenixLexicon extends Lexicon {
    public YelpPhoenixLexicon() {
        //// Lexicon for points of interest
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "restaurants");
                add(Restaurants.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "some food");
                add(Food.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bars");
                add(Bars.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mexican restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mexican places");
                add(Mexican.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mexican food");
                add(Mexican.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "american restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "american restaurants");
                add(AmericanTraditional.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "american food");
                add(AmericanTraditional.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "fast food restaurants");
                add(FastFood.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fast food");
                add(FastFood.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pizza");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pizza restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pizza restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pizzeria");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pizzerias");
                add(Pizza.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "travel agency");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "kiosk");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "travel guide");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "travel agencies");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "kiosks");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "travel guides");
                add(HotelsAndTravel.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich shop");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich store");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sandwich restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sandwich shops");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sandwich stores");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sandwich restaurants");
                add(Sandwiches.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "coffee shop");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "coffee shops");
                add(CoffeeAndTea.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern american restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "modern american restaurants");
                add(AmericanNew.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian restaurants");
                add(Italian.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "italian place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "italian places");
                add(Italian.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "chinese restaurants");
                add(Chinese.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "chinese place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "chinese places");
                add(Chinese.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hotel");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "motel");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "inn");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hotels");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "motels");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "inns");
                add(Hotels.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hamburger restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hamburger restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hamburger stand");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hamburger stands");
                add(Burgers.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "burger restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "burger restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "burger joint");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hamburger joint");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "burger joints");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hamburger joints");
                add(Burgers.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "grocer");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "grocers");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "grocery store");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "grocery stores");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "supermarket");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "supermarkets");
                add(Grocery.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "breakfast restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "breakfast restaurants");
                add(BreakfastAndBrunch.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "breakfast food");
                add(BreakfastAndBrunch.class, entry, true);
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
                add(IceCreamAndFrozenYogurt.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "specialty restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "specialty restaurants");
                add(SpecialtyFood.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bakery");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bakeries");
                add(Bakeries.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pub");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pubs");
                add(Pubs.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "japanese restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "japanese restaurants");
                add(Japanese.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sports bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sports bars");
                add(SportsBars.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "convenience store");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "convenience stores");
                add(ConvenienceStores.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "deli");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "delis");
                add(Delis.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sushi bars");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sushi restaurants");
                add(SushiBars.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sushi place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sushi places");
                add(SushiBars.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "steak house");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "steakhouse");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "steak houses");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "steakhouses");
                add(Steakhouses.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cafe");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cafes");
                add(Cafes.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "seafood restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "seafood restaurants");
                add(Seafood.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "seafood place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "seafood places");
                add(Seafood.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert shop");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dessert shops");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dessert restaurants");
                add(Desserts.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dessert place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dessert places");
                add(Desserts.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "buffet");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "buffets");
                add(Buffets.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "barbeque");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "barbeques");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bbq");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bbqs");
                add(Barbeque.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "thai restaurants");
                add(Thai.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "thai places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "thai food");
                add(Thai.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mediterranean restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mediterranean restaurants");
                add(Mediterranean.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mediterranean place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "mediterranean places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "mediterranean food");
                add(Mediterranean.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "spirits");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wine and spirits");
                add(BeerWineAndSpirits.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wings");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wings place");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wing stop");
                add(ChickenWings.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "asian fusion restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "asian fusion restaurants");
                add(AsianFusion.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "asian fusion place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "asian fusion places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "asian fusion food");
                add(AsianFusion.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "smoothie shop");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "smoothie shops");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "juice bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "juice bars");
                add(JuiceBarsAndSmoothies.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "greek restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "greek places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "greek food");
                add(Greek.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "indian restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "indian places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "indian food");
                add(Indian.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tex mex restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tex mex places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tex mex food");
                add(TexMex.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "donut shop");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "donut shops");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "donut store");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "donut stores");
                add(Donuts.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "diner");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "diners");
                add(Diners.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog stand");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hot dog stands");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hot dog restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hot dog place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hot dog places");
                add(HotDogs.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vietnamese restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vietnamese places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vietnamese food");
                add(Vietnamese.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "wine bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "wine bars");
                add(WineBars.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "local restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "local place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "local places");
                add(LocalFlavor.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "salad restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "salad places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "salad food");
                add(Salad.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dive bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dive bars");
                add(DiveBars.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegetarian restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegetarian places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetarian food");
                add(Vegetarian.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "british restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "british places");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "british food");
                add(British.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "french restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "french restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "french food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "french place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "french places");
                add(French.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bagel restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bagel restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bagels");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bagel");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "bagel place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "bagel places");
                add(Bagels.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "korean restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "korean restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "korean food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "korean place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "korean places");
                add(Korean.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ethnic restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "ethnic restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ethnic food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "ethnic place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "ethnic places");
                add(EthnicFood.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hawaiian restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hawaiian restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hawaiian food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hawaiian place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hawaiian places");
                add(Hawaiian.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "catering");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "caterer");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "caterers");
                add(Caterers.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gluten free restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gluten free restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gluten free food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gluten free place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gluten free places");
                add(GlutenFree.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "middle eastern restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "middle eastern restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "middle eastern food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "middle eastern place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "middle eastern places");
                add(MiddleEastern.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "farmers market");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "farmers markets");
                add(FarmersMarket.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gastro pub");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gastro pubs");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gastropub");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gastropubs");
                add(Gastropubs.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "latin american restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "latin american restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "latin american food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "latin american place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "latin american places");
                add(LatinAmerican.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "food truck");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "food trucks");
                add(FoodTrucks.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "karaoke place");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "karaoke");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "karaoke places");
                add(Karaoke.class, entry, false);
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
                add(CandyStores.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "brewery");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "breweries");
                add(Breweries.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fish and chips");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fish n' chips");
                add(FishAndChips.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegan restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegan restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegan food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegan place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegan places");
                add(Vegan.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "gay bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "gay bars");
                add(GayBars.class, entry, false);
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
                add(ChocolatiersAndShops.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "food delivery service");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "food delivery");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "food delivery services");
                add(FoodDeliveryServices.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pakistani restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pakistani restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pakistani food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "pakistani place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "pakistani places");
                add(Pakistani.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shaved ice restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "shaved ice restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shaved ice");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "shaved ice place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "shaved ice places");
                add(ShavedIce.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "food stand");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "food stands");
                add(FoodStands.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "filipino restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "filipino restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "filipino food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "filipino place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "filipino places");
                add(Filipino.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cocktail bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cocktail bars");
                add(CocktailBars.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "southern restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "southern restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "southern food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "southern place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "southern places");
                add(Southern.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hookah bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hookah bars");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "hookah place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "hookah places");
                add(HookahBars.class, entry, false);
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
                add(CajunCreole.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "irish restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "irish restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "irish food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "irish place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "irish places");
                add(Irish.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tea room");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tea rooms");
                add(TeaRooms.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soul food restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "soul food restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soul food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soul food place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "soul food places");
                add(SoulFood.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soup restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "soup restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soup");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "soup place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "soup places");
                add(Soup.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "caribbean restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "caribbean restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "caribbean food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "caribbean place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "caribbean places");
                add(Caribbean.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "spanish restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "spanish restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "spanish food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "spanish place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "spanish places");
                add(Spanish.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tapas restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tapas restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tapas");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tapas place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tapas places");
                add(TapasSmallPlates.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "fruit");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "fruits");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "veggies");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "vegetables");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "vegetable");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "veggie");
                add(FruitsAndVeggies.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheesesteak restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cheesesteak restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheesesteak");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cheesesteaks");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheesesteak place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "cheesesteak places");
                add(Cheesesteaks.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "tapas bar");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "tapas bars");
                add(TapasBars.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "sports club");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "sports clubs");
                add(SportsClubs.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dim sum restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dim sum restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dim sum");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "dim sum place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "dim sum places");
                add(DimSum.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "comfort food");
                add(ComfortFood.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern european restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "modern european restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern european food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "modern european place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "modern european places");
                add(ModernEuropean.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "scottish restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "scottish restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "scottish food");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "scottish place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "scottish places");
                add(Scottish.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "crepe restaurant");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "crepe restaurants");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "creperie");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "creperies");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "crepe place");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "crepe places");
                add(Creperies.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheese shop");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "cheese shops");
                add(CheeseShops.class, entry, false);
            }

        }

        //// Lexicon for adjectives
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "cheap");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "inexpensive");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "affordable");
                add(Cheap.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "cheapest");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "cheaper");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "most inexpensive");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "most affordable");
                add(Cheap.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "expensive");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "pricey");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "costly");
                add(Expensive.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "most expensive");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "more expensive");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "pricier");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "priciest");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "costliest");
                add(Expensive.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "good");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "excellent");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "highly rated");
                add(Good.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "better");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "best");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "highest rated");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "higher rated");
                add(Good.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "hip");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "popular");
                add(Popular.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "en vogue");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "hippest");
                entry.add(LexicalEntry.PART_OF_SPEECH.ADJECTIVE, "most popular");
                add(Popular.class, entry, true);
            }
        }

        //// Lexicon for prepositions
        {
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "close to");
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "near to");
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "near");
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "by");
                add(IsCloseTo.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "on");
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "closest to");
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "closest");
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "nearest to");
                entry.add(LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE, "nearest");
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
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "make a reservation");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "book a reservation");
                entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "making a reservation");
                entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "booking a reservation");
                entry.add(LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, "reservation");
                entry.add(LexicalEntry.PART_OF_SPEECH.PLURAL_NOUN, "reservations");
                add(MakeReservation.class, entry, false);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "make reservation");
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "book reservation");
                entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "making reservation");
                entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "booking a reservation");
                add(MakeReservation.class, entry, true);
            }
            {
                LexicalEntry entry = new LexicalEntry();
                entry.add(LexicalEntry.PART_OF_SPEECH.S1_VERB, "give directions");
                entry.add(LexicalEntry.PART_OF_SPEECH.PRESENT_PROGRESSIVE_VERB, "giving directions");
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
