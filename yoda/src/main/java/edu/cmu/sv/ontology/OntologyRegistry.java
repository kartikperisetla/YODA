package edu.cmu.sv.ontology;

import edu.cmu.sv.ontology.action.Exist;
import edu.cmu.sv.ontology.action.Verb;
import edu.cmu.sv.ontology.action.Create;
import edu.cmu.sv.ontology.action.HasProperty;
import edu.cmu.sv.ontology.object.*;
import edu.cmu.sv.ontology.property.HasName;
import edu.cmu.sv.ontology.property.Property;
import edu.cmu.sv.ontology.role.Agent;
import edu.cmu.sv.ontology.role.Patient;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.ontology.role.Theme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 9/22/14.
 */
public class OntologyRegistry {
    public static Set<Class <? extends Verb>> actionClasses = new HashSet<>();
    public static Set<Class <? extends edu.cmu.sv.ontology.object.Object>> objectClasses = new HashSet<>();
    public static Set<Class <? extends Role>> roleClasses = new HashSet<>();
    public static Set<Class <? extends Property>> propertyClasses = new HashSet<>();
    public static Map<String, Class <? extends Verb>> verbNameMap = new HashMap<>();
    public static Map<String, Class <? extends Role>> roleNameMap = new HashMap<>();

    static{
        // register leaf classes

        actionClasses.add(Create.class);
        actionClasses.add(HasProperty.class);
        actionClasses.add(Exist.class);

        objectClasses.add(Person.class);
        objectClasses.add(Email.class);
        objectClasses.add(Meeting.class);

        roleClasses.add(Agent.class);
        roleClasses.add(Patient.class);
        roleClasses.add(Theme.class);

        propertyClasses.add(HasName.class);

        //todo: recursively register parents

        // get name maps
        // todo: check that there are no naming conflicts
        for (Class<? extends Verb> actionClass : actionClasses){
            verbNameMap.put(actionClass.getSimpleName(), actionClass);
        }
        for (Class<? extends Role> roleClass : roleClasses){
            roleNameMap.put(roleClass.getSimpleName(), roleClass);
        }

    }

}
