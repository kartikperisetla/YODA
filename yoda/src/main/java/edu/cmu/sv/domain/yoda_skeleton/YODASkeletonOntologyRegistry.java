package edu.cmu.sv.domain.yoda_skeleton;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.Destination;
import edu.cmu.sv.domain.yelp_phoenix.ontology.role.Origin;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.*;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Person;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Time;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.*;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Create;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Exist;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YODASkeletonOntologyRegistry extends OntologyRegistry{

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

    public YODASkeletonOntologyRegistry() {
        // register classes
        verbClasses.add(Verb.class);
        verbClasses.add(Create.class);
        verbClasses.add(HasProperty.class);
        verbClasses.add(Exist.class);

        nounClasses.add(Noun.class);
        nounClasses.add(Person.class);
        nounClasses.add(Time.class);
        nounClasses.add(PointOfInterest.class);

        roleClasses.add(Role.class);
        roleClasses.add(Agent.class);
        roleClasses.add(Patient.class);
        roleClasses.add(HasAtTime.class);
        roleClasses.add(HasHour.class);
        roleClasses.add(HasName.class);
        roleClasses.add(HasValues.class);
        roleClasses.add(HasValue.class);
        roleClasses.add(HasURI.class);
        roleClasses.add(InRelationTo.class);
        roleClasses.add(Origin.class);
        roleClasses.add(Destination.class);

        miscClasses.add(NonHearing.class);
        miscClasses.add(NonUnderstanding.class);
        miscClasses.add(Requested.class);
        miscClasses.add(Suggested.class);
        miscClasses.add(UnknownThingWithRoles.class);
        miscClasses.add(Or.class);
        miscClasses.add(And.class);
        miscClasses.add(WebResource.class);

    }

}
