package edu.cmu.sv.domain.scotty;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.scotty.ontology.nouns.poi_types.*;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Off;
import edu.cmu.sv.domain.smart_house.ontology.adjective.On;
import edu.cmu.sv.domain.smart_house.ontology.noun.AirConditioner;
import edu.cmu.sv.domain.smart_house.ontology.noun.Appliance;
import edu.cmu.sv.domain.smart_house.ontology.noun.Room;
import edu.cmu.sv.domain.smart_house.ontology.noun.SecuritySystem;
import edu.cmu.sv.domain.smart_house.ontology.quality.PowerState;
import edu.cmu.sv.domain.smart_house.ontology.role.Component;
import edu.cmu.sv.domain.smart_house.ontology.role.HasPowerState;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOnAppliance;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Cheap;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Expensive;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Good;
import edu.cmu.sv.domain.yelp_phoenix.ontology.adjective.Popular;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
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
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
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
public class ScottyOntologyRegistry extends OntologyRegistry{

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

    public ScottyOntologyRegistry() {


        verbClasses.add(GiveDirections.class);

        nounClasses.add(PointOfInterest.class);

        nounClasses.add(Bank.class);
        nounClasses.add(Bar.class);
        nounClasses.add(Bench.class);
        nounClasses.add(BicycleParking.class);
        nounClasses.add(Cafe.class);
        nounClasses.add(FastFood.class);
        nounClasses.add(GarbageCan.class);
        nounClasses.add(GasStation.class);
        nounClasses.add(GraveYard.class);
        nounClasses.add(Hospital.class);
        nounClasses.add(Kindergarten.class);
        nounClasses.add(MailBox.class);
        nounClasses.add(Parking.class);
        nounClasses.add(Pharmacy.class);
        nounClasses.add(PlaceOfWorship.class);
        nounClasses.add(PostOffice.class);
        nounClasses.add(PublicBuilding.class);
        nounClasses.add(PublicTelephone.class);
        nounClasses.add(Recycling.class);
        nounClasses.add(Restaurant.class);
        nounClasses.add(Restroom.class);
        nounClasses.add(School.class);
        nounClasses.add(Shelter.class);

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
