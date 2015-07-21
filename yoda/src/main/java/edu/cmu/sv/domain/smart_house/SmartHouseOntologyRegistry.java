package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.ontology.*;
import edu.cmu.sv.domain.ontology.query_fragments.BinaryRelationQueryFragment;
import edu.cmu.sv.domain.ontology.query_fragments.OrderedStringsQueryFragment;
import edu.cmu.sv.domain.ontology.query_fragments.ScaledShiftedSingleValueQueryFragment;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by David Cohen on 3/3/15.
 */
public class SmartHouseOntologyRegistry implements OntologyRegistry{

    public Set<Verb> verbs = new HashSet<>();
    public Set<Role> roles = new HashSet<>();
    public Set<Noun> nouns = new HashSet<>();
    public Set<Quality> qualities = new HashSet<>();
    public Set<QualityDegree> qualityDegrees = new HashSet<>();

    @Override
    public Set<Verb> getVerbs() {
        return verbs;
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public Set<Noun> getNouns() {
        return nouns;
    }

    @Override
    public Set<Quality> getQualities() {
        return qualities;
    }

    @Override
    public Set<QualityDegree> getQualityDegrees() {
        return qualityDegrees;
    }

    public static Role component = new Role("Component", false, false);
    public static Role hasRoom = new Role("HasRoom", false, false);

    public static Noun appliance = new Noun("Appliance", YodaSkeletonOntologyRegistry.physicalNoun);
    public static Noun airConditioner = new Noun("AirConditioner", appliance);
    public static Noun securitySystem = new Noun("SecuritySystem", appliance);
    public static Noun thermostat = new Noun("Thermostat", appliance);
    public static Noun microwave = new Noun("Microwave", appliance);
    public static Noun roomba = new Noun("Roomba", appliance);
    public static Noun room = new Noun("Room", YodaSkeletonOntologyRegistry.place);
    public static Noun kitchen = new Noun("Kitchen", room);
    public static Noun livingRoom = new Noun("LivingRoom", room);

    public static Quality cleanliness = new Quality("Cleanliness", room, null,
            new ScaledShiftedSingleValueQueryFragment("dust_level", 0.0, 5.0, true));
    public static QualityDegree clean = new QualityDegree("Clean", 1.0, 2.0, cleanliness);
    public static QualityDegree dirty = new QualityDegree("Dirty", 0.0, 1.0, cleanliness);

    public static Quality powerState = new Quality("PowerState", appliance, null,
            new OrderedStringsQueryFragment("power_state", Arrays.asList("off", "on")));
    public static QualityDegree on = new QualityDegree("On", 1.0, 100.0, powerState);
    public static QualityDegree off = new QualityDegree("Off", 0.0, 100.0, powerState);

    public static Quality containedBy = new Quality("ContainedBy", appliance, room,
            new BinaryRelationQueryFragment("in_room"));
    public static QualityDegree isContainedBy = new QualityDegree("IsContainedBy", 1.0, 100.0, containedBy);


    public static Verb turnOnAppliance = new Verb("TurnOnAppliance", Arrays.asList(component), new LinkedList<>());
    public static Verb turnOffAppliance = new Verb("TurnOffAppliance", Arrays.asList(component), new LinkedList<>());
    public static Verb cleanRoom = new Verb("CleanRoom", Arrays.asList(hasRoom), new LinkedList<>());

    static {
        component.getDomain().addAll(Arrays.asList(turnOnAppliance, turnOffAppliance));
        component.getRange().addAll(Arrays.asList(appliance));
        hasRoom.getDomain().addAll(Arrays.asList(cleanRoom));
        hasRoom.getRange().addAll(Arrays.asList(room));
    }

    public SmartHouseOntologyRegistry() {
        verbs.add(turnOnAppliance);
        verbs.add(turnOffAppliance);
        verbs.add(cleanRoom);

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

        qualities.add(cleanliness);
        qualities.add(powerState);
        qualities.add(containedBy);

        qualityDegrees.add(clean);
        qualityDegrees.add(dirty);
        qualityDegrees.add(on);
        qualityDegrees.add(off);
        qualityDegrees.add(isContainedBy);
    }

}
