package edu.cmu.sv.database;

import edu.cmu.sv.domain.OntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.WebResource;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasURI;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.has_quality_subroles.HasQualityRole;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/22/14.
 */
public class Ontology {
    public static Set<Class <? extends Verb>> verbClasses = new HashSet<>();
    public static Set<Class <? extends Noun>> nounClasses = new HashSet<>();
    public static Set<Class <? extends Adjective>> adjectiveClasses = new HashSet<>();
    public static Set<Class <? extends Preposition>> prepositionClasses = new HashSet<>();
    public static Set<Class <? extends Role>> roleClasses = new HashSet<>();
    public static Set<Class <? extends TransientQuality>> qualityClasses = new HashSet<>();
    public static Set<Class <? extends Thing>> miscClasses = new HashSet<>();

    public static Map<String, Class <? extends Thing>> thingNameMap = new HashMap<>();
    public static Map<String, Class <? extends Verb>> verbNameMap = new HashMap<>();
    public static Map<String, Class <? extends Role>> roleNameMap = new HashMap<>();
    public static Map<String, Thing> individualNameMap = new HashMap<>();

    public static Map<Class<? extends ThingWithRoles>, Set<Class<? extends TransientQuality>>> qualitiesForClass = new HashMap<>();

    public static void loadOntologyRegistry(OntologyRegistry registry){
        verbClasses.addAll(registry.getVerbClasses());
        nounClasses.addAll(registry.getNounClasses());
        adjectiveClasses.addAll(registry.getAdjectiveClasses());
        prepositionClasses.addAll(registry.getPrepositionClasses());
        roleClasses.addAll(registry.getRoleClasses());
        qualityClasses.addAll(registry.getQualityClasses());
        miscClasses.addAll(registry.getMiscClasses());
    }


    public static void finalizeOntology(){
        // recursively register parents
        recursivelyRegisterParents(verbClasses);
        recursivelyRegisterParents(nounClasses);
        recursivelyRegisterParents(roleClasses);
//        recursivelyRegisterParents(qualityClasses);
        recursivelyRegisterParents(miscClasses);

        // register individuals

        // add ubiquitous Things to domains / ranges
        for (Class <? extends ThingWithRoles> cls : Arrays.asList(UnknownThingWithRoles.class)){
            for (Class <? extends Role> roleCls : roleClasses){
                if (roleCls==Role.class)
                    continue;
                try {
                    roleCls.newInstance().getDomain().add(cls);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        // get name maps
        addToNameMap(verbNameMap, verbClasses);
        addToNameMap(roleNameMap, roleClasses);
        addToNameMap(thingNameMap, verbClasses);
        addToNameMap(thingNameMap, nounClasses);
        addToNameMap(thingNameMap, adjectiveClasses);
        addToNameMap(thingNameMap, prepositionClasses);
        addToNameMap(thingNameMap, roleClasses);
        addToNameMap(thingNameMap, miscClasses);
        addToNameMap(thingNameMap, qualityClasses);

        // register qualities for class
        for (Class<? extends TransientQuality> qualityClass : qualityClasses){
            Set<Class<? extends ThingWithRoles>> qualityDom = qualityDomain(qualityClass);
            for (Class<? extends ThingWithRoles> thingCls : qualityDom){
                if (!qualitiesForClass.containsKey(thingCls))
                    qualitiesForClass.put(thingCls, new HashSet<>());
                qualitiesForClass.get(thingCls).add(qualityClass);
            }
        }
    }


    /*
    * Return the set of ThingWithRole classes that can be described by the given quality class
    * */
    public static Set<Class< ? extends ThingWithRoles>> qualityDomain(Class<? extends TransientQuality> qualityClass){
        Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> qualityDescriptor = qualityDescriptors(qualityClass);
        return thingNameMap.values().stream().
                filter(x -> !Modifier.isAbstract(x.getModifiers())).
                filter(ThingWithRoles.class::isAssignableFrom).
                filter(x -> inDomain(qualityDescriptor.getKey(), (Class<? extends ThingWithRoles>) x)).
                map(x -> (Class<? extends ThingWithRoles>) x).
                collect(Collectors.toSet());
    }

    /*
    * Return the List of Thing classes that are arguments of the given quality
    * */
    public static List<Class< ? extends Thing>> qualityArguments(Class<? extends TransientQuality> qualityClass){
        try {
            return qualityClass.newInstance().getArguments();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new Error("can't determine the quality class's arguments");
    }

    /*
    * Returns the HasQualityRole, and the set of quality descriptors (Adjectives + Prepositions)
    * corresponding to a given quality class
    * */
    public static Pair<Class<? extends Role>, Set<Class<? extends ThingWithRoles>>> qualityDescriptors(
            Class<? extends TransientQuality> qualityClass){
        if (Modifier.isAbstract(qualityClass.getModifiers()))
            return null;
        try {
            Set<Class<? extends ThingWithRoles>> adjectiveAndPrepositionClasses = new HashSet<>();
            for (Class<? extends Adjective> cls : adjectiveClasses){
                if (cls.newInstance().getQuality().equals(qualityClass)) {
                    adjectiveAndPrepositionClasses.add(cls);
                }
            }
            for (Class<? extends Preposition> cls : prepositionClasses){
                if (cls.newInstance().getQuality().equals(qualityClass)){
                    adjectiveAndPrepositionClasses.add(cls);
                }
            }
            Class<? extends Role> roleClass = roleClasses.stream().
                    filter(HasQualityRole.class::isAssignableFrom).
                    filter(x -> inRange(x, new LinkedList<>(adjectiveAndPrepositionClasses).get(0))).
                    collect(Collectors.toList()).get(0);
            return new ImmutablePair<>(roleClass, adjectiveAndPrepositionClasses);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new Error("Ontology.qualityDescriptors: instantiation or access exception:" + qualityClass);
        }
    }

    public static boolean inDomain(Class<? extends Role> roleClass, Class<? extends ThingWithRoles> subjectClass){
        try {
            if (Modifier.isAbstract(roleClass.getModifiers()))
                return false;
            return roleClass.newInstance().getDomain().stream().anyMatch(x -> x.isAssignableFrom(subjectClass));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new Error("inDomain:failed to init object");
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean inRange(Class<? extends Role> roleClass , Class<? extends Thing> objectClass){
        try {
            if (Modifier.isAbstract(roleClass.getModifiers()))
                return false;
            return roleClass.newInstance().getRange().stream().anyMatch(x -> x.isAssignableFrom(objectClass));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new Error("inRange:failed to init object"+objectClass);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static <S,T> void addToNameMap(Map<String, Class <? extends S>> nameMap, Set<Class <? extends T>> classSet){
        for (Class <? extends T> cls : classSet){
            String id = cls.getSimpleName();
            if (nameMap.keySet().contains(id)){
                throw new ValueException("NAMING CONFLICT: The class name:"+id+" is already registered.");
            }
            nameMap.put(id, (Class<? extends S>) cls);
        }
    }

    public static <T> void recursivelyRegisterParents(Set<Class <? extends T>> classSet){
        List<Class> queue = new LinkedList<>(classSet);
        while (!queue.isEmpty()) {
            Class cls = queue.get(0);
            queue.remove(cls);
            Class<? extends T> superCls = cls.getSuperclass();
            if (!Modifier.isAbstract(superCls.getModifiers())) {
                classSet.add(superCls);
                queue.add(superCls);
            }
        }
    }

    public static Class<? extends Thing> adjectiveOrPrepositionInRange(Class<? extends Role> roleClass){
        try {
            Set<Class<? extends Thing>> range = roleClass.newInstance().getRange();
            for (Class<? extends Thing> rangeClass : range){
                if (Adjective.class.isAssignableFrom(rangeClass))
                    return Adjective.class;
                if (Preposition.class.isAssignableFrom(rangeClass))
                    return Preposition.class;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public static Class<? extends TransientQuality> qualityInRolesRange(Class<? extends Role> roleClass){
        try {
            Set<Class<? extends Thing>> range = roleClass.newInstance().getRange();
            for (Class<? extends Thing> rangeClass : range){
                if (Adjective.class.isAssignableFrom(rangeClass)) {
                    if (Modifier.isAbstract(rangeClass.getModifiers())) {
                        for (Class<? extends Adjective> adjectiveClass : adjectiveClasses) {
                            if (rangeClass.isAssignableFrom(adjectiveClass))
                                return adjectiveClass.newInstance().getQuality();
                        }
                    }
                    throw new Error("the requested adjective class has no descendants registered");
                }
                if (Preposition.class.isAssignableFrom(rangeClass)){
                    if (Modifier.isAbstract(rangeClass.getModifiers())) {
                        for (Class<? extends Preposition> prepositionClass : prepositionClasses) {
                            if (rangeClass.isAssignableFrom(prepositionClass))
                                return prepositionClass.newInstance().getQuality();
                        }
                    }
                    throw new Error("the requested preposition class has no descendants registered");
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public static String webResourceWrap(String URI){
        String ans = "{\"class\": \""+ WebResource.class.getSimpleName()+"\", \""+
                HasURI.class.getSimpleName()+"\":\""+URI+"\"}";
        return ans;
    }

}
