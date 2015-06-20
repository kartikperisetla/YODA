package edu.cmu.sv.database;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.ontology.*;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by David Cohen on 9/22/14.
 */
public class Ontology {
    public static Set<Role> roles = new HashSet<>();
    public static Set<Verb> verbs = new HashSet<>();
    public static Set<Noun> nouns = new HashSet<>();
    public static Set<Quality> qualities = new HashSet<>();
    public static Set<QualityDegree> qualityDegrees = new HashSet<>();

    public static Map<String, Role> roleNameMap = new HashMap<>();
    public static Map<String, Verb> verbNameMap = new HashMap<>();
    public static Map<String, Noun> nounNameMap = new HashMap<>();
    public static Map<String, Quality> qualityNameMap = new HashMap<>();
    public static Map<String, QualityDegree> qualityDegreeNameMap = new HashMap<>();
    public static Map<String, Object> thingNameMap = new HashMap<>();

    public static Map<Object, Set<Quality>> qualitiesForClass = new HashMap<>();

    public static void loadOntologyRegistry(OntologyRegistry registry){
        verbs.addAll(registry.getVerbs());
        nouns.addAll(registry.getNouns());
        qualityDegrees.addAll(registry.getQualityDegrees());
        qualities.addAll(registry.getQualities());
        roles.addAll(registry.getRoles());
    }


    public static void finalizeOntology() {

        // create has-quality roles
        for (Quality qualityClass : qualities){
            Role hasQualityRole = new Role("Has"+qualityClass.name, true);
            hasQualityRole.getDomain().addAll(Arrays.asList(qualityClass.firstArgumentClassConstraint));
            hasQualityRole.getRange().addAll(Arrays.asList(qualityClass.secondArgumentClassConstraint));
            roles.add(hasQualityRole);
        }

        // finalize Roles
        for (Role role : roles){
            role.getDomain().addAll(Arrays.asList(YodaSkeletonOntologyRegistry.unknownThingWithRoles));
        }

        // populate name maps
        verbs.forEach(x -> verbNameMap.put(x.name, x));
        roles.forEach(x -> roleNameMap.put(x.name, x));
        nouns.forEach(x -> nounNameMap.put(x.name, x));
        qualities.forEach(x -> qualityNameMap.put(x.name, x));
        qualityDegrees.forEach(x -> qualityDegreeNameMap.put(x.name, x));

        verbs.forEach(x -> thingNameMap.put(x.name, x));
        roles.forEach(x -> thingNameMap.put(x.name, x));
        nouns.forEach(x -> thingNameMap.put(x.name, x));
        qualityDegrees.forEach(x -> thingNameMap.put(x.name, x));
        qualities.forEach(x -> thingNameMap.put(x.name, x));

        // register qualities for class
        nouns.stream().forEach(x -> qualitiesForClass.put(x, new HashSet<>()));
        for (Quality quality : qualities){
            for (Noun noun: nouns){
                if (nounInherits(quality.firstArgumentClassConstraint, noun))
                    qualitiesForClass.get(noun).add(quality);
            }
        }
    }

    public static boolean nounInherits(Noun parent, Noun child){
        if (parent==child)
            return true;
        Noun directParent = child.directParent;
        while (directParent != null){
            if (directParent==parent)
                return true;
            directParent = directParent.directParent;
        }
        return false;
    }

    /*
    * Returns the HasQualityRole, and the set of QualityDegree instances
    * corresponding to a given quality class
    * */
    public static Pair<Role, Set<QualityDegree>> qualityDescriptors(Quality quality){
        Role qualityRole = roleNameMap.get("Has"+quality.name);
        Set<QualityDegree> qualityDegrees = new HashSet<>(quality.getQualityDegrees());
        return new ImmutablePair<>(qualityRole, qualityDegrees);
    }

    public static boolean inDomain(Role roleInstance, Object subjectClass){
        if (subjectClass instanceof Verb){
            return roleInstance.getDomain().contains(subjectClass);
        } else if (subjectClass instanceof Noun){
            return roleInstance.getDomain().stream().filter(x -> x instanceof Noun).
                    anyMatch(x -> nounInherits((Noun) x, (Noun) subjectClass));
        }
        throw new ValueException("Ontology.inDomain. Can't handle this request:" + roleInstance.name + ", "+subjectClass.getClass().getSimpleName());
    }


    public static boolean inRange(Role roleInstance, Object subjectClass){
        if (subjectClass instanceof Verb){
            return roleInstance.getRange().contains(subjectClass);
        } else if (subjectClass instanceof Noun){
            return roleInstance.getRange().stream().filter(x -> x instanceof Noun).
                    anyMatch(x -> nounInherits((Noun) x, (Noun) subjectClass));
        }
        throw new ValueException("Ontology.inRange. Can't handle this request:" + roleInstance.name + ", "+subjectClass.getClass().getSimpleName());
    }

    public static String webResourceWrap(String URI){
        return  "{\"class\": \""+ YodaSkeletonOntologyRegistry.webResource.name+"\", \"HasURI\":\""+URI+"\"}";
    }

}
