package edu.cmu.sv.domain.yoda_skeleton;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.ontology.*;

import java.util.*;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YodaSkeletonOntologyRegistry implements OntologyRegistry{

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

    // define misc 'nouns'
    public static Noun nonHearing = new Noun("NonHearing", null);
    public static Noun nonUnderstanding = new Noun("NonUnderstanding", null);
    public static Noun requested = new Noun("Requested", null);
    public static Noun suggested = new Noun("Suggested", null);
    public static Noun webResource = new Noun("WebResource", null);

    // the skeleton ontology roles are public & static so that they can be referred to in code easily
    // this is needed to define their lexical information
    public static Role agent = new Role("Agent", false);
    public static Role patient = new Role("Patient", false);
    public static Role hasAtTime = new Role("HasAtTime", false);
    public static Role hasHour = new Role("HasHour", false);
    public static Role hasTenMinute = new Role("HasTenMinute", false);
    public static Role hasSingleMinute = new Role("HasSingleMinute", false);
    public static Role hasAmPm = new Role("HasAmPm", false);
    public static Role hasName = new Role("HasName", false);
    public static Role hasValue = new Role("HasValue", false);
    public static Role hasUri = new Role("HasURI", false);
    // todo: every preposition should be automatically added to inRelationTo's domain
    public static Role inRelationTo = new Role("InRelationTo", false);


    // define root noun hierarchy
    public static Noun unknownThingWithRoles = new Noun("UnknownThingWithRoles", null);
    public static Noun rootNoun = new Noun("Noun", unknownThingWithRoles);
    public static Noun physicalNoun = new Noun("PhysicalNoun", rootNoun);
    public static Noun nonPhysicalNoun = new Noun("NonPhysicalNoun", rootNoun);
    public static Noun person = new Noun("Person", physicalNoun);
    public static Noun timeNounClass = new Noun("Time", nonPhysicalNoun);
    public static Noun place = new Noun("Place", physicalNoun);


    // define verbs
    public static Verb hasProperty = new Verb("HasProperty", Arrays.asList(agent), Arrays.asList(patient));
    public static Verb exist = new Verb("Exist", new LinkedList<>(), Arrays.asList(agent));


    static{
        // finalize the domain and range of roles
        agent.getDomain().addAll(Arrays.asList(hasProperty, exist));
        agent.getRange().addAll(Arrays.asList(rootNoun));
        patient.getDomain().addAll(Arrays.asList(hasProperty));
        patient.getRange().addAll(Arrays.asList(person));

        // todo: hasAtTime should allow any verb, but I currently have no verb hierarchy
        hasAtTime.getDomain().addAll(Arrays.asList(hasProperty, exist));
        hasAtTime.getRange().addAll(Arrays.asList(timeNounClass));
        hasHour.getDomain().addAll(Arrays.asList(timeNounClass));
        hasHour.getRange().addAll(Arrays.asList(webResource));
        hasTenMinute.getDomain().addAll(Arrays.asList(timeNounClass));
        hasTenMinute.getRange().addAll(Arrays.asList(webResource));
        hasSingleMinute.getDomain().addAll(Arrays.asList(timeNounClass));
        hasSingleMinute.getRange().addAll(Arrays.asList(webResource));
        hasAmPm.getDomain().addAll(Arrays.asList(timeNounClass));
        hasAmPm.getRange().addAll(Arrays.asList(webResource));
        hasName.getDomain().addAll(Arrays.asList(rootNoun));
        hasName.getRange().addAll(Arrays.asList(webResource));
        hasValue.getDomain().addAll(Arrays.asList(suggested, requested));
        hasValue.getRange().addAll(Arrays.asList(rootNoun));
        hasUri.getDomain().addAll(Arrays.asList(webResource));
        hasUri.getRange().addAll(Arrays.asList());
        inRelationTo.getDomain().addAll(Arrays.asList());
        inRelationTo.getRange().addAll(Arrays.asList(rootNoun));
    }

    public YodaSkeletonOntologyRegistry() {
        verbs.add(hasProperty);
        verbs.add(exist);

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

    }
}
