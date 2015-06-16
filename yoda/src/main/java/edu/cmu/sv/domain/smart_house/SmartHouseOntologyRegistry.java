package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.ontology2.*;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Clean;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Dirty;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Off;
import edu.cmu.sv.domain.smart_house.ontology.adjective.On;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Hot;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Cold;
import edu.cmu.sv.domain.smart_house.ontology.noun.*;
import edu.cmu.sv.domain.smart_house.ontology.preposition.IsContainedBy;
import edu.cmu.sv.domain.smart_house.ontology.quality.Cleanliness;
import edu.cmu.sv.domain.smart_house.ontology.quality.ContainedBy;
import edu.cmu.sv.domain.smart_house.ontology.quality.PowerState;
import edu.cmu.sv.domain.smart_house.ontology.quality.Temperature;
import edu.cmu.sv.domain.smart_house.ontology.role.*;
import edu.cmu.sv.domain.smart_house.ontology.verb.CleanRoom;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOffAppliance;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOnAppliance;
import edu.cmu.sv.domain.smart_house.ontology.verb.IncreaseTemperature;
import edu.cmu.sv.domain.smart_house.ontology.verb.DecreaseTemperature;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 3/3/15.
 */
public class SmartHouseOntologyRegistry implements OntologyRegistry{

    public Set<Verb2> verbs = new HashSet<>();
    public Set<Role2> roles = new HashSet<>();
    public Set<Noun2> nouns = new HashSet<>();
    public Set<Quality2> qualities = new HashSet<>();
    public Set<QualityDegree> qualityDegrees = new HashSet<>();

    @Override
    public Set<Verb2> getVerbs() {
        return verbs;
    }

    @Override
    public Set<Role2> getRoles() {
        return roles;
    }

    @Override
    public Set<Noun2> getNouns() {
        return nouns;
    }

    @Override
    public Set<Quality2> getQualities() {
        return qualities;
    }

    @Override
    public Set<QualityDegree> getQualityDegrees() {
        return qualityDegrees;
    }

    public static Role2 component = new Role2("Component", false);
    public static Role2 hasRoom = new Role2("HasRoom", false);

    public static Noun2 appliance = new Noun2("Appliance", YodaSkeletonOntologyRegistry.physicalNoun);
    public static Noun2 airConditioner = new Noun2("AirConditioner", appliance);
    public static Noun2 securitySystem = new Noun2("SecuritySystem", appliance);
    public static Noun2 thermostat = new Noun2("Thermostat", appliance);
    public static Noun2 microwave = new Noun2("Microwave", appliance);
    public static Noun2 roomba = new Noun2("Roomba", appliance);
    public static Noun2 room = new Noun2("Room", YodaSkeletonOntologyRegistry.place);
    public static Noun2 kitchen = new Noun2("Kitchen", room);
    public static Noun2 livingRoom = new Noun2("LivingRoom", room);

    static {
        component.getDomain().addAll(Arrays.asList(turnOnAppliance, turnOffAppliance));
        component.getRange().addAll(Arrays.asList(appliance));
        hasRoom.getDomain().addAll(Arrays.asList(cleanRoom));
        hasRoom.getRange().addAll(Arrays.asList(room));
    }

    public SmartHouseOntologyRegistry() {
        nouns.add(appliance);
        nouns.add(airConditioner);
        nouns.add(securitySystem);
        nouns.add(thermostat);
        nouns.add(microwave);
        nouns.add(roomba);
        nouns.add(room);
        nouns.add(kitchen);
        nouns.add(livingRoom);

        roles.add(component);
        roles.add(hasRoom);


        roleClasses.add(Component.class);
        roleClasses.add(HasRoom.class);

        adjectiveClasses.add(On.class);
        adjectiveClasses.add(Off.class);
        adjectiveClasses.add(Clean.class);
        adjectiveClasses.add(Dirty.class);
        adjectiveClasses.add(Hot.class);
        adjectiveClasses.add(Cold.class);

        prepositionClasses.add(IsContainedBy.class);

        verbClasses.add(TurnOnAppliance.class);
        verbClasses.add(TurnOffAppliance.class);
        verbClasses.add(CleanRoom.class);
        verbClasses.add(IncreaseTemperature.class);
        verbClasses.add(DecreaseTemperature.class);

        qualityClasses.add(PowerState.class);
        qualityClasses.add(Cleanliness.class);
        qualityClasses.add(ContainedBy.class);
        qualityClasses.add(Temperature.class);
    }

}
