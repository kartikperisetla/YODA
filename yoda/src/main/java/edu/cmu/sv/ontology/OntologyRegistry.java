package edu.cmu.sv.ontology;

import edu.cmu.sv.ontology.action.Exist;
import edu.cmu.sv.ontology.action.Verb;
import edu.cmu.sv.ontology.action.Create;
import edu.cmu.sv.ontology.action.HasProperty;
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
    public static Set<Class <? extends Role>> roleClasses = new HashSet<>();
    public static Map<String, Class <? extends Verb>> verbNameMap = new HashMap<>();
    public static Map<String, Class <? extends Role>> roleNameMap = new HashMap<>();

    static{
        actionClasses.add(Create.class);
        actionClasses.add(HasProperty.class);
        actionClasses.add(Exist.class);

        roleClasses.add(Agent.class);
        roleClasses.add(Patient.class);
        roleClasses.add(Theme.class);

        for (Class<? extends Verb> actionClass : actionClasses){
            verbNameMap.put(actionClass.getSimpleName(), actionClass);
        }
        for (Class<? extends Role> roleClass : roleClasses){
            roleNameMap.put(roleClass.getSimpleName(), roleClass);
        }

    }

}
