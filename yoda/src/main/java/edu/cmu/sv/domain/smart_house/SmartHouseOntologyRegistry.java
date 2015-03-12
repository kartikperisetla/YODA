package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.OntologyRegistry;
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
        nounClasses.add(Room.class);

        roleClasses.add(HasPowerState.class);
        roleClasses.add(Component.class);

        adjectiveClasses.add(On.class);
        adjectiveClasses.add(Off.class);

        verbClasses.add(TurnOnAppliance.class);

        qualityClasses.add(PowerState.class);
    }

}