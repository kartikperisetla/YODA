package edu.cmu.sv.domain.yoda_skeleton;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.*;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2.Noun2;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Person;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Time;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2.Quality2;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2.Role2;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Create;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Exist;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YodaSkeletonOntologyRegistry implements OntologyRegistry{

    public Set<Class <? extends Verb>> verbClasses = new HashSet<>();
    public Set<Class <? extends Noun>> nounClasses = new HashSet<>();
    public Set<Class <? extends Adjective>> adjectiveClasses = new HashSet<>();
    public Set<Class <? extends Preposition>> prepositionClasses = new HashSet<>();
    public Set<Class <? extends Quality2>> qualityClasses = new HashSet<>();
    public Set<Class <? extends Thing>> miscClasses = new HashSet<>();
    public Set<Role2> roles = new HashSet<>();
    public Set<Noun2> nouns = new HashSet<>();

    @Override
    public Set<Noun2> getNouns() {
        return nouns;
    }

    @Override
    public Set<Role2> getRoles() {
        return roles;
    }

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

    public Set<Class< ? extends Quality2>> getQualityClasses() {
        return qualityClasses;
    }

    public Set<Class<? extends Thing>> getMiscClasses() {
        return miscClasses;
    }

    // the skeleton ontology roles are public & static so that they can be referred to in code easily
    // this is needed to define their lexical information
    public static Role2 agent = new Role2("Agent", new HashSet<>(Arrays.asList(HasProperty.class, Exist.class)),
            new HashSet<>(Arrays.asList(Noun.class)), false);
    public static Role2 patient = new Role2("Patient", new HashSet<>(Arrays.asList(HasProperty.class, HasProperty.class)),
            new HashSet<>(Arrays.asList(Person.class)), false);
    public static Role2 hasAtTime = new Role2("HasAtTime", new HashSet<>(Arrays.asList(Verb.class)),
            new HashSet<>(Arrays.asList(Time.class)), false);
    public static Role2 hasHour = new Role2("HasHour", new HashSet<>(Arrays.asList(Time.class)),
            new HashSet<>(Arrays.asList(WebResource.class)), false);
    public static Role2 hasTenMinute = new Role2("HasTenMinute", new HashSet<>(Arrays.asList(Time.class)),
            new HashSet<>(Arrays.asList(WebResource.class)), false);
    public static Role2 hasSingleMinute = new Role2("HasSingleMinute", new HashSet<>(Arrays.asList(Time.class)),
            new HashSet<>(Arrays.asList(WebResource.class)), false);
    public static Role2 hasAmPm = new Role2("HasAmPm", new HashSet<>(Arrays.asList(Time.class)),
            new HashSet<>(Arrays.asList(WebResource.class)), false);
    public static Role2 hasName = new Role2("HasName", new HashSet<>(Arrays.asList(Noun.class)),
            new HashSet<>(Arrays.asList(Time.class)), false);
    public static Role2 hasValue = new Role2("HasValue", new HashSet<>(Arrays.asList(Suggested.class, Requested.class)),
            new HashSet<>(Arrays.asList(Thing.class)), false);
    public static Role2 hasUri = new Role2("HasURI", new HashSet<>(Arrays.asList(WebResource.class)),
            new HashSet<>(), false);
    public static Role2 inRelationTo = new Role2("InRelationTo", new HashSet<>(Arrays.asList(Preposition.class)),
            new HashSet<>(Arrays.asList(Thing.class)), false);




    // define root noun hierarchy
    public static Noun2 rootNoun = new Noun2("Noun", null);
    public static Noun2 physicalNoun = new Noun2("PhysicalNoun", rootNoun);
    public static Noun2 nonPhysicalNoun = new Noun2("NonPhysicalNoun", rootNoun);
    public static Noun2 person = new Noun2("Person", physicalNoun);
    public static Noun2 timeNounClass = new Noun2("Time", nonPhysicalNoun);
    public static Noun2 place = new Noun2("Place", physicalNoun);


    public YodaSkeletonOntologyRegistry() {
        // register classes
        verbClasses.add(Verb.class);
        verbClasses.add(Create.class);
        verbClasses.add(HasProperty.class);
        verbClasses.add(Exist.class);

        nouns.add(rootNoun);
        nouns.add(physicalNoun);
        nouns.add(nonPhysicalNoun);
        nouns.add(person);
        nouns.add(timeNounClass);
        nouns.add(place);

        roles.add(agent);
        roles.add(patient);
        roles.add(hasAtTime);
        roles.add(hasHour);
        roles.add(hasTenMinute);
        roles.add(hasSingleMinute);
        roles.add(hasAmPm);
        roles.add(hasName);
        roles.add(hasValue);
        roles.add(hasUri);
        roles.add(inRelationTo);

        miscClasses.add(NonHearing.class);
        miscClasses.add(NonUnderstanding.class);
        miscClasses.add(Requested.class);
        miscClasses.add(Suggested.class);
        miscClasses.add(UnknownThingWithRoles.class);
        miscClasses.add(WebResource.class);

    }

}
