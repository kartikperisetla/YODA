package edu.cmu.sv.ontology;

import edu.cmu.sv.ontology.misc.NonHearing;
import edu.cmu.sv.ontology.misc.NonUnderstanding;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.verb.Exist;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.ontology.verb.Create;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.object.*;
import edu.cmu.sv.ontology.role.Agent;
import edu.cmu.sv.ontology.role.Patient;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.role.Theme;
import edu.cmu.sv.semantics.SemanticsModel;
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

        verbClasses.add(Create.class);
        verbClasses.add(HasProperty.class);
        verbClasses.add(Exist.class);

        objectClasses.add(Person.class);
        objectClasses.add(Email.class);
        objectClasses.add(Meeting.class);

        roleClasses.add(Agent.class);
        roleClasses.add(Patient.class);
        roleClasses.add(Theme.class);

        miscClasses.add(NonHearing.class);
        miscClasses.add(NonUnderstanding.class);
        miscClasses.add(Requested.class);

        // recursively register parents
        recursivelyRegisterParents(verbClasses);
        recursivelyRegisterParents(objectClasses);
        recursivelyRegisterParents(roleClasses);
        recursivelyRegisterParents(miscClasses);

        // get name maps
        addToNameMap(verbNameMap, verbClasses);
        addToNameMap(roleNameMap, roleClasses);
        addToNameMap(thingNameMap, verbClasses);
        addToNameMap(thingNameMap, objectClasses);
        addToNameMap(thingNameMap, roleClasses);
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

    // TODO: validate DST models
    /*
    * Determines whether the SLU hypothesis model is valid
    * according to the registered ontology
    * */
    public static boolean validateSLUHypothesis(SemanticsModel model) {
        // there must be a dialog act
        if (!model.getSlots().containsKey("dialogAct"))
            return false;
        // there is a specific set of permitted slots in the top level
        if (model.getSlots().keySet().stream().
                anyMatch(x -> !x.equals("dialogAct") && !x.equals("verb") && !x.equals("topic")))
            return false;

        // check all children
        for (SemanticsModel child : model.getChildren().values()){
            try {
                if (!validateThingDescription(child))
                    return false;
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /*
    * Recursively check that all the slots in a node are valid for the class of that node
    * (does not check that the fillers are in the range for the role which corresponds to that slot)
    * (assumes that the input is a well-formed SemanticsModel (no orphans, etc.))
    * */
    public static boolean validateThingDescription(SemanticsModel description) throws IllegalAccessException, InstantiationException {
        if (!description.getSlots().containsKey("class"))
            return false;
        Class<? extends Thing> cls = thingNameMap.get(description.getSlots().get("class"));
        // check that all slots correspond to roles which this node's class is in the domain of
        for (String slot : description.getSlots().keySet()){
            if (slot.equals("class"))
                continue;
            boolean inDomain = false;
            if (!roleNameMap.containsKey(slot))
                return false;
            for (Class<? extends Thing> domainMember : roleNameMap.get(slot).newInstance().getDomain()){
                if (domainMember.isAssignableFrom(cls)) {
                    inDomain = true;
                    break;
                }
            }
            if (!inDomain)
                return false;
        }

        // check all children
        for (SemanticsModel child : description.getChildren().values()){
            if (!validateThingDescription(child))
                return false;
        }

        return true;
    }


}
