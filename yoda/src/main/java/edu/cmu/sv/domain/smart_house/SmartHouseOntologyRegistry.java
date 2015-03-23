package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.smart_house.ontology.adjective.Off;
import edu.cmu.sv.domain.smart_house.ontology.adjective.On;
import edu.cmu.sv.domain.smart_house.ontology.noun.*;
import edu.cmu.sv.domain.smart_house.ontology.preposition.IsContainedBy;
import edu.cmu.sv.domain.smart_house.ontology.quality.ContainedBy;
import edu.cmu.sv.domain.smart_house.ontology.quality.PowerState;
import edu.cmu.sv.domain.smart_house.ontology.role.Component;
import edu.cmu.sv.domain.smart_house.ontology.role.HasContainedByState;
import edu.cmu.sv.domain.smart_house.ontology.role.HasPowerState;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOffAppliance;
import edu.cmu.sv.domain.smart_house.ontology.verb.TurnOnAppliance;
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
public class SmartHouseOntologyRegistry extends OntologyRegistry{

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

    public SmartHouseOntologyRegistry() {
        nounClasses.add(Appliance.class);
        nounClasses.add(AirConditioner.class);
        nounClasses.add(SecuritySystem.class);
        nounClasses.add(Thermostat.class);
        nounClasses.add(Microwave.class);
        nounClasses.add(Roomba.class);
        nounClasses.add(Room.class);
        nounClasses.add(Kitchen.class);
        nounClasses.add(LivingRoom.class);

        roleClasses.add(HasPowerState.class);
        roleClasses.add(HasContainedByState.class);
        roleClasses.add(Component.class);

        adjectiveClasses.add(On.class);
        adjectiveClasses.add(Off.class);

        prepositionClasses.add(IsContainedBy.class);

        verbClasses.add(TurnOnAppliance.class);
        verbClasses.add(TurnOffAppliance.class);

        qualityClasses.add(PowerState.class);
        qualityClasses.add(ContainedBy.class);
    }

}
