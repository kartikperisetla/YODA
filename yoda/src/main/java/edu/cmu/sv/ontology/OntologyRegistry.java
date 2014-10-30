package edu.cmu.sv.ontology;

import edu.cmu.sv.ontology.misc.*;
import edu.cmu.sv.ontology.object.Object;
import edu.cmu.sv.ontology.object.poi_types.*;
import edu.cmu.sv.ontology.role.*;
import edu.cmu.sv.ontology.verb.Exist;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.verb.Create;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.object.*;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by David Cohen on 9/22/14.
 */
public class OntologyRegistry {
    public static Set<Class <? extends Verb>> verbClasses = new HashSet<>();
    public static Set<Class <? extends edu.cmu.sv.ontology.object.Object>> objectClasses = new HashSet<>();
    public static Set<Class <? extends Role>> roleClasses = new HashSet<>();
    public static Set<Class <? extends Thing>> miscClasses = new HashSet<>();

    public static Map<String, Class <? extends Thing>> thingNameMap = new HashMap<>();
    public static Map<String, Class <? extends Verb>> verbNameMap = new HashMap<>();
    public static Map<String, Class <? extends Role>> roleNameMap = new HashMap<>();

    static{
        // register leaf classes

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
        roleClasses.add(HasHour.class);
        roleClasses.add(HasName.class);
        roleClasses.add(HasValues.class);
        roleClasses.add(HasValue.class);
        roleClasses.add(HasURI.class);
        roleClasses.add(IsCloseTo.class);

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
        recursivelyRegisterParents(miscClasses);

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
        addToNameMap(thingNameMap, miscClasses);
    }

    public static boolean existsAClassInRangeOfAll(Set<Class <? extends Role>> roles){
        Set<Class> possibleClasses = new HashSet<>(thingNameMap.values());
        possibleClasses.remove(UnknownThingWithRoles.class);
        for (Class<? extends Role> roleClass : roles){
            try {
                possibleClasses.retainAll(roleClass.newInstance().getDomain());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("OntologyRegistry.existsAClassInRangeOfAll: possibleClasses:"+possibleClasses);
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
