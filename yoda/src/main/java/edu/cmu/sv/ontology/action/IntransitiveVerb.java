package edu.cmu.sv.ontology.action;

import edu.cmu.sv.ontology.role.Agent;
import edu.cmu.sv.ontology.role.Role;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/22/14.
 */
public class IntransitiveVerb extends Verb {
    public Set<Class <? extends Role>> getRequiredRoles(){
        return new HashSet<>(Arrays.asList(Agent.class));
    }
}
