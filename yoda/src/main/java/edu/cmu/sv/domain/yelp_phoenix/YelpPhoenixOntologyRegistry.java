package edu.cmu.sv.domain.yelp_phoenix;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Cheap;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Expensive;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Good;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Popular;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.poi_types.*;
import edu.cmu.sv.domain.yelp_phoenix.ontology.preposition.IsCloseTo;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.binary_quality.Distance;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality.Expensiveness;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality.Goodness;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality.Popularity;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.has_quality_subroles.HasDistance;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.has_quality_subroles.HasExpensiveness;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.has_quality_subroles.HasGoodness;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.has_quality_subroles.HasPopularity;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.GiveDirections;
import edu.cmu.sv.domain.yelp_phoenix.ontology.verb.MakeReservation;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Email;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Meeting;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YelpPhoenixOntologyRegistry extends OntologyRegistry{

    public Set<Class <? extends Verb>> verbClasses = new HashSet<>();
    public Set<Class <? extends Noun>> nounClasses = new HashSet<>();
    public Set<Class <? extends Adjective>> adjectiveClasses = new HashSet<>();
    public Set<Class <? extends Preposition>> prepositionClasses = new HashSet<>();
    public Set<Class <? extends Role>> roleClasses = new HashSet<>();
    public Set<Class <? extends TransientQuality>> qualityClasses = new HashSet<>();
    public Set<Class <? extends Thing>> miscClasses = new HashSet<>();

    public Set<Class<? extends Verb>> getVerbClasses() {
        return verbClasses;
    }

    public Set<Class<? extends Noun>> getNounClasses() {
        return nounClasses;
    }

    public Set<Class<? extends Adjective>> getAdjectiveClasses() {
        return adjectiveClasses;
    }

    public Set<Class<? extends Preposition>> getPrepositionClasses() {
        return prepositionClasses;
    }

    public Set<Class<? extends Role>> getRoleClasses() {
        return roleClasses;
    }

    public Set<Class<? extends TransientQuality>> getQualityClasses() {
        return qualityClasses;
    }

    public Set<Class<? extends Thing>> getMiscClasses() {
        return miscClasses;
    }

    public YelpPhoenixOntologyRegistry() {
        // register classes
        verbClasses.add(GiveDirections.class);
        verbClasses.add(MakeReservation.class);

        nounClasses.add(Email.class);
        nounClasses.add(Meeting.class);

        nounClasses.add(Restaurants.class);
        nounClasses.add(Food.class);
        nounClasses.add(Bars.class);
        nounClasses.add(Mexican.class);
        nounClasses.add(AmericanTraditional.class);
        nounClasses.add(FastFood.class);
        nounClasses.add(Pizza.class);
        nounClasses.add(HotelsAndTravel.class);
        nounClasses.add(Sandwiches.class);
        nounClasses.add(CoffeeAndTea.class);
        nounClasses.add(AmericanNew.class);
        nounClasses.add(Italian.class);
        nounClasses.add(Chinese.class);
        nounClasses.add(Hotels.class);
        nounClasses.add(Burgers.class);
        nounClasses.add(Grocery.class);
        nounClasses.add(BreakfastAndBrunch.class);
        nounClasses.add(IceCreamAndFrozenYogurt.class);
        nounClasses.add(SpecialtyFood.class);
        nounClasses.add(Bakeries.class);
        nounClasses.add(Pubs.class);
        nounClasses.add(Japanese.class);
        nounClasses.add(SportsBars.class);
        nounClasses.add(ConvenienceStores.class);
        nounClasses.add(Delis.class);
        nounClasses.add(SushiBars.class);
        nounClasses.add(Steakhouses.class);
        nounClasses.add(Cafes.class);
        nounClasses.add(Seafood.class);
        nounClasses.add(Desserts.class);
        nounClasses.add(Buffets.class);
        nounClasses.add(Barbeque.class);
        nounClasses.add(Thai.class);
        nounClasses.add(Mediterranean.class);
        nounClasses.add(BeerWineAndSpirits.class);
        nounClasses.add(ChickenWings.class);
        nounClasses.add(AsianFusion.class);
        nounClasses.add(JuiceBarsAndSmoothies.class);
        nounClasses.add(Greek.class);
        nounClasses.add(Indian.class);
        nounClasses.add(TexMex.class);
        nounClasses.add(Donuts.class);
        nounClasses.add(Diners.class);
        nounClasses.add(HotDogs.class);
        nounClasses.add(Vietnamese.class);
        nounClasses.add(WineBars.class);
        nounClasses.add(LocalFlavor.class);
        nounClasses.add(Salad.class);
        nounClasses.add(DiveBars.class);
        nounClasses.add(Vegetarian.class);
        nounClasses.add(British.class);
        nounClasses.add(French.class);
        nounClasses.add(Bagels.class);
        nounClasses.add(Korean.class);
        nounClasses.add(EthnicFood.class);
        nounClasses.add(Hawaiian.class);
        nounClasses.add(Caterers.class);
        nounClasses.add(GlutenFree.class);
        nounClasses.add(MiddleEastern.class);
        nounClasses.add(FarmersMarket.class);
        nounClasses.add(Gastropubs.class);
        nounClasses.add(LatinAmerican.class);
        nounClasses.add(FoodTrucks.class);
        nounClasses.add(Karaoke.class);
        nounClasses.add(CandyStores.class);
        nounClasses.add(Breweries.class);
        nounClasses.add(FishAndChips.class);
        nounClasses.add(Vegan.class);
        nounClasses.add(GayBars.class);
        nounClasses.add(ChocolatiersAndShops.class);
        nounClasses.add(FoodDeliveryServices.class);
        nounClasses.add(Pakistani.class);
        nounClasses.add(ShavedIce.class);
        nounClasses.add(FoodStands.class);
        nounClasses.add(Filipino.class);
        nounClasses.add(CocktailBars.class);
        nounClasses.add(Southern.class);
        nounClasses.add(HookahBars.class);
        nounClasses.add(CajunCreole.class);
        nounClasses.add(Irish.class);
        nounClasses.add(TeaRooms.class);
        nounClasses.add(SoulFood.class);
        nounClasses.add(Soup.class);
        nounClasses.add(Caribbean.class);
        nounClasses.add(Spanish.class);
        nounClasses.add(TapasSmallPlates.class);
        nounClasses.add(FruitsAndVeggies.class);
        nounClasses.add(Cheesesteaks.class);
        nounClasses.add(TapasBars.class);
        nounClasses.add(SportsClubs.class);
        nounClasses.add(DimSum.class);
        nounClasses.add(ComfortFood.class);
        nounClasses.add(ModernEuropean.class);
        nounClasses.add(Scottish.class);
        nounClasses.add(Creperies.class);
        nounClasses.add(CheeseShops.class);

        roleClasses.add(HasDistance.class);
        roleClasses.add(HasExpensiveness.class);
        roleClasses.add(HasGoodness.class);
        roleClasses.add(HasPopularity.class);

        adjectiveClasses.add(Cheap.class);
        adjectiveClasses.add(Expensive.class);
        adjectiveClasses.add(Good.class);
        adjectiveClasses.add(Popular.class);

        prepositionClasses.add(IsCloseTo.class);

        qualityClasses.add(Expensiveness.class);
        qualityClasses.add(Goodness.class);
        qualityClasses.add(Popularity.class);
        qualityClasses.add(Distance.class);
    }

}
