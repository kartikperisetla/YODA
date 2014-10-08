package edu.cmu.sv.ontology;

import edu.cmu.sv.ontology.misc.NonHearing;
import edu.cmu.sv.ontology.misc.NonUnderstanding;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.verb.Exist;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.verb.Create;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.object.*;
import edu.cmu.sv.ontology.property.HasName;
import edu.cmu.sv.ontology.property.Property;
import edu.cmu.sv.ontology.role.Agent;
import edu.cmu.sv.ontology.role.Patient;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.role.Theme;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by David Cohen on 9/22/14.
 */
public class OntologyRegistry {
    public static Set<Class <? extends Verb>> verbClasses = new HashSet<>();
    public static Set<Class <? extends edu.cmu.sv.ontology.object.Object>> objectClasses = new HashSet<>();
    public static Set<Class <? extends Role>> roleClasses = new HashSet<>();
    public static Set<Class <? extends Property>> propertyClasses = new HashSet<>();
    public static Set<Class <? extends Thing>> miscClasses = new HashSet<>();

    public static Map<String, Class <? extends Thing>> thingNameMap = new HashMap<>();
    public static Map<String, Class <? extends Verb>> verbNameMap = new HashMap<>();
    public static Map<String, Class <? extends Role>> roleNameMap = new HashMap<>();

    static{
        // register leaf classes

        verbClasses.add(Create.class);
        verbClasses.add(HasProperty.class);
        verbClasses.add(Exist.class);

        objectClasses.add(Person.class);
        objectClasses.add(Email.class);
        objectClasses.add(Meeting.class);

        roleClasses.add(Agent.class);
        roleClasses.add(Patient.class);
        roleClasses.add(Theme.class);

        propertyClasses.add(HasName.class);

        miscClasses.add(NonHearing.class);
        miscClasses.add(NonUnderstanding.class);
        miscClasses.add(Requested.class);

        // recursively register parents
        recursivelyRegisterParents(verbClasses);
        recursivelyRegisterParents(objectClasses);
        recursivelyRegisterParents(roleClasses);
        recursivelyRegisterParents(propertyClasses);
        recursivelyRegisterParents(miscClasses);

        // get name maps
        addToNameMap(verbNameMap, verbClasses);
        addToNameMap(roleNameMap, roleClasses);
        addToNameMap(thingNameMap, verbClasses);
        addToNameMap(thingNameMap, objectClasses);
        addToNameMap(thingNameMap, roleClasses);
        addToNameMap(thingNameMap, propertyClasses);
        addToNameMap(thingNameMap, miscClasses);
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

}
