package edu.cmu.sv.domain.yoda_skeleton;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.yelp_phoenix.ontology.noun.PointOfInterest;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.*;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.*;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.*;
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
    public Set<Class <? extends TransientQuality>> qualityClasses = new HashSet<>();
    public Set<Class <? extends Thing>> miscClasses = new HashSet<>();
    public Set<Role2> roles = new HashSet<>();

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

    public Set<Class<? extends TransientQuality>> getQualityClasses() {
        return qualityClasses;
    }

    public Set<Class<? extends Thing>> getMiscClasses() {
        return miscClasses;
    }

    public YodaSkeletonOntologyRegistry() {
        // register classes
        verbClasses.add(Verb.class);
        verbClasses.add(Create.class);
        verbClasses.add(HasProperty.class);
        verbClasses.add(Exist.class);

        nounClasses.add(Noun.class);
        nounClasses.add(PhysicalNoun.class);
        nounClasses.add(NonPhysicalNoun.class);
        nounClasses.add(Person.class);
        nounClasses.add(Time.class);
        nounClasses.add(PointOfInterest.class);

//        roleClasses.add(Role.class);
        roles.add(new Role2("Agent", new HashSet<>(Arrays.asList(HasProperty.class, Exist.class)),
                new HashSet<>(Arrays.asList(Noun.class)), ));
        roles.add(new Role2("Patient", new HashSet<>(Arrays.asList(HasProperty.class, HasProperty.class)),
                new HashSet<>(Arrays.asList(Person.class)), ));
        roles.add(new Role2("HasAtTime", new HashSet<>(Arrays.asList(Verb.class)),
                new HashSet<>(Arrays.asList(Time.class)), ));
        roles.add(new Role2("HasHour", new HashSet<>(Arrays.asList(Time.class)),
                new HashSet<>(Arrays.asList(WebResource.class)), ));
        roles.add(new Role2("HasTenMinute", new HashSet<>(Arrays.asList(Time.class)),
                new HashSet<>(Arrays.asList(WebResource.class)), ));
        roles.add(new Role2("HasSingleMinute", new HashSet<>(Arrays.asList(Time.class)),
                new HashSet<>(Arrays.asList(WebResource.class)), ));
        roles.add(new Role2("HasAmPm", new HashSet<>(Arrays.asList(Time.class)),
                new HashSet<>(Arrays.asList(WebResource.class)), ));
        roles.add(new Role2("HasName", new HashSet<>(Arrays.asList(Noun.class)),
                new HashSet<>(Arrays.asList(Time.class)), ));
        roles.add(new Role2("HasValue", new HashSet<>(Arrays.asList(Suggested.class, Requested.class)),
                new HashSet<>(Arrays.asList(Thing.class)), ));
        roles.add(new Role2("HasURI", new HashSet<>(Arrays.asList(WebResource.class)),
                new HashSet<>(), ));
        roles.add(new Role2("InRelationTo", new HashSet<>(Arrays.asList(Preposition.class)),
                new HashSet<>(Arrays.asList(Thing.class)), ));


        miscClasses.add(NonHearing.class);
        miscClasses.add(NonUnderstanding.class);
        miscClasses.add(Requested.class);
        miscClasses.add(Suggested.class);
        miscClasses.add(UnknownThingWithRoles.class);
        miscClasses.add(WebResource.class);

    }

}
