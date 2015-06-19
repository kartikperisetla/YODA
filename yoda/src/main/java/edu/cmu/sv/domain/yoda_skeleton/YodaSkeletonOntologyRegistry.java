package edu.cmu.sv.domain.yoda_skeleton;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.ontology2.*;

import java.util.*;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YodaSkeletonOntologyRegistry implements OntologyRegistry{

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

    // define misc 'nouns'
    public static Noun2 nonHearing = new Noun2("NonHearing", null);
    public static Noun2 nonUnderstanding = new Noun2("NonUnderstanding", null);
    public static Noun2 requested = new Noun2("Requested", null);
    public static Noun2 suggested = new Noun2("Suggested", null);
    public static Noun2 unknownThingWithRoles = new Noun2("UnknownThingWithRoles", null);
    public static Noun2 webResource = new Noun2("WebResource", null);

    // the skeleton ontology roles are public & static so that they can be referred to in code easily
    // this is needed to define their lexical information
    public static Role2 agent = new Role2("Agent", false);
    public static Role2 patient = new Role2("Patient", false);
    public static Role2 hasAtTime = new Role2("HasAtTime", false);
    public static Role2 hasHour = new Role2("HasHour", false);
    public static Role2 hasTenMinute = new Role2("HasTenMinute", false);
    public static Role2 hasSingleMinute = new Role2("HasSingleMinute", false);
    public static Role2 hasAmPm = new Role2("HasAmPm", false);
    public static Role2 hasName = new Role2("HasName", false);
    public static Role2 hasValue = new Role2("HasValue", false);
    public static Role2 hasUri = new Role2("HasURI", false);
    // todo: every preposition should be automatically added to inRelationTo's domain
    public static Role2 inRelationTo = new Role2("InRelationTo", false);


    // define root noun hierarchy
    public static Noun2 rootNoun = new Noun2("Noun", null);
    public static Noun2 physicalNoun = new Noun2("PhysicalNoun", rootNoun);
    public static Noun2 nonPhysicalNoun = new Noun2("NonPhysicalNoun", rootNoun);
    public static Noun2 person = new Noun2("Person", physicalNoun);
    public static Noun2 timeNounClass = new Noun2("Time", nonPhysicalNoun);
    public static Noun2 place = new Noun2("Place", physicalNoun);


    // define verbs
    public static Verb2 hasProperty = new Verb2("HasProperty", Arrays.asList(agent), Arrays.asList(patient));
    public static Verb2 exist = new Verb2("Exist", new LinkedList<>(), Arrays.asList(agent));


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
