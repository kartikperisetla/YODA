package edu.cmu.sv.ontology;

import edu.cmu.sv.ontology.absolute_quality_degree.*;
import edu.cmu.sv.ontology.misc.*;
import edu.cmu.sv.ontology.object.Object;
import edu.cmu.sv.ontology.object.poi_types.*;
import edu.cmu.sv.ontology.quality.Expensiveness;
import edu.cmu.sv.ontology.quality.Quality;
import edu.cmu.sv.ontology.role.*;
import edu.cmu.sv.ontology.role.has_quality_subroles.HasAbsoluteQualityDegree;
import edu.cmu.sv.ontology.role.has_quality_subroles.HasExpensiveness;
import edu.cmu.sv.ontology.role.has_quality_subroles.HasHeight;
import edu.cmu.sv.ontology.verb.Exist;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.verb.Create;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.object.*;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 9/22/14.
 */
public class OntologyRegistry {
    public static Set<Class <? extends Verb>> verbClasses = new HashSet<>();
    public static Set<Class <? extends edu.cmu.sv.ontology.object.Object>> objectClasses = new HashSet<>();
    public static Set<Class <? extends Role>> roleClasses = new HashSet<>();
    public static Set<Class <? extends Quality>> qualityClasses = new HashSet<>();
    public static Set<Class <? extends AbsoluteQualityDegree>> absoluteQualityDegreeClasses = new HashSet<>();
    public static Set<Class <? extends HasAbsoluteQualityDegree>> qualityRoleClasses = new HashSet<>();
    public static Set<Class <? extends Thing>> miscClasses = new HashSet<>();

    public static Map<String, Class <? extends Thing>> thingNameMap = new HashMap<>();
    public static Map<String, Class <? extends Verb>> verbNameMap = new HashMap<>();
    public static Map<String, Class <? extends Role>> roleNameMap = new HashMap<>();
    public static Map<String, Thing> individualNameMap = new HashMap<>();

    static{
        // register classes
        verbClasses.add(Verb.class);
        verbClasses.add(Create.class);
        verbClasses.add(HasProperty.class);
        verbClasses.add(Exist.class);

        objectClasses.add(Object.class);
        objectClasses.add(Person.class);
        objectClasses.add(Email.class);
        objectClasses.add(Meeting.class);
        objectClasses.add(Time.class);
        objectClasses.add(PointOfInterest.class);

        objectClasses.add(Bank.class);
        objectClasses.add(Bar.class);
        objectClasses.add(Bench.class);
        objectClasses.add(BicycleParking.class);
        objectClasses.add(Cafe.class);
        objectClasses.add(FastFood.class);
        objectClasses.add(GarbageCan.class);
        objectClasses.add(GasStation.class);
        objectClasses.add(GraveYard.class);
        objectClasses.add(Hospital.class);
        objectClasses.add(Kindergarten.class);
        objectClasses.add(MailBox.class);
        objectClasses.add(Parking.class);
        objectClasses.add(Pharmacy.class);
        objectClasses.add(PlaceOfWorship.class);
        objectClasses.add(PostOffice.class);
        objectClasses.add(PublicBuilding.class);
        objectClasses.add(PublicTelephone.class);
        objectClasses.add(Recycling.class);
        objectClasses.add(Restaurant.class);
        objectClasses.add(Restroom.class);
        objectClasses.add(School.class);
        objectClasses.add(Shelter.class);

        roleClasses.add(Role.class);
        roleClasses.add(Agent.class);
        roleClasses.add(Patient.class);
        roleClasses.add(Theme.class);
        roleClasses.add(HasAtTime.class);
//        roleClasses.add(HasAbsoluteQualityDegree.class);
        roleClasses.add(HasHour.class);
        roleClasses.add(HasName.class);
        roleClasses.add(HasValues.class);
        roleClasses.add(HasValue.class);
        roleClasses.add(HasURI.class);
        roleClasses.add(IsCloseTo.class);

        qualityClasses.add(Quality.class);
        qualityClasses.add(Expensiveness.class);

        absoluteQualityDegreeClasses.add(Expensive.class);
        absoluteQualityDegreeClasses.add(Cheap.class);
        absoluteQualityDegreeClasses.add(Tall.class);
        absoluteQualityDegreeClasses.add(edu.cmu.sv.ontology.absolute_quality_degree.Short.class);

        qualityRoleClasses.add(HasExpensiveness.class);
        qualityRoleClasses.add(HasHeight.class);

        miscClasses.add(NonHearing.class);
        miscClasses.add(NonUnderstanding.class);
        miscClasses.add(Requested.class);
        miscClasses.add(Suggested.class);
        miscClasses.add(UnknownThingWithRoles.class);
        miscClasses.add(Or.class);
        miscClasses.add(And.class);
        miscClasses.add(WebResource.class);

        // recursively register parents
        recursivelyRegisterParents(verbClasses);
        recursivelyRegisterParents(objectClasses);
        recursivelyRegisterParents(roleClasses);
        recursivelyRegisterParents(qualityClasses);
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
        addToNameMap(thingNameMap, objectClasses);
        addToNameMap(thingNameMap, roleClasses);
        addToNameMap(thingNameMap, qualityClasses);
        addToNameMap(thingNameMap, miscClasses);
    }

    public static boolean inDomain(Class<? extends Role> roleClass, Class<? extends ThingWithRoles> subjectClass){
        try {
            return roleClass.newInstance().getDomain().stream().anyMatch(x -> x.isAssignableFrom(subjectClass));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            return false;
//            System.exit(0);
        }
        return false;
    }

    public static boolean inRange(Class<? extends Role> roleClass, Class<? extends ThingWithRoles> subjectClass){
        try {
            return roleClass.newInstance().getRange().stream().anyMatch(x -> x.isAssignableFrom(subjectClass));
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            return false;
//            System.exit(0);
        }
        return false;
    }


    public static boolean existsAClassInDomainOfAll(Set<Class<? extends Role>> roles){
        Set<Class> possibleClasses = new HashSet<>(thingNameMap.values());
        possibleClasses.remove(UnknownThingWithRoles.class);
        for (Class<? extends Role> roleClass : roles){
            possibleClasses.retainAll((Set)
                    possibleClasses.stream().
                    filter(x -> inDomain(roleClass, x)).
                    collect(Collectors.toSet()));
        }
        return !possibleClasses.isEmpty();
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

    public static String WebResourceWrap(String URI){
        String ans = "{\"class\": \""+ WebResource.class.getSimpleName()+"\", \""+
                HasURI.class.getSimpleName()+"\":\""+URI+"\"}";
        return ans;
    }

}
