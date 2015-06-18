package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.ontology2.*;
import edu.cmu.sv.domain.ontology2.query_fragments.BinaryRelationQueryFragment;
import edu.cmu.sv.domain.ontology2.query_fragments.OrderedStringsQueryFragment;
import edu.cmu.sv.domain.ontology2.query_fragments.ScaledShiftedSingleValueQueryFragment;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
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

    public static Quality2 cleanliness = new Quality2("Cleanliness", room, null,
            new ScaledShiftedSingleValueQueryFragment("dust_level", 0.0, 5.0, true));
    public static QualityDegree clean = new QualityDegree("Clean", 1.0, 2.0, cleanliness);
    public static QualityDegree dirty = new QualityDegree("Dirty", 0.0, 1.0, cleanliness);

    public static Quality2 powerState = new Quality2("PowerState", appliance, null,
            new OrderedStringsQueryFragment("power_state", Arrays.asList("off", "on")));
    public static QualityDegree on = new QualityDegree("On", 1.0, 100.0, powerState);
    public static QualityDegree off = new QualityDegree("Off", 0.0, 100.0, powerState);

    public static Quality2 containedBy = new Quality2("ContainedBy", appliance, room,
            new BinaryRelationQueryFragment("in_room"));
    public static QualityDegree isContainedBy = new QualityDegree("IsContainedBy", 1.0, 100.0, containedBy);


    public static Verb2 turnOnAppliance = new Verb2("TurnOnAppliance", Arrays.asList(component), new LinkedList<>());
    public static Verb2 turnOffAppliance = new Verb2("TurnOffAppliance", Arrays.asList(component), new LinkedList<>());
    public static Verb2 cleanRoom = new Verb2("CleanRoom", Arrays.asList(hasRoom), new LinkedList<>());

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
