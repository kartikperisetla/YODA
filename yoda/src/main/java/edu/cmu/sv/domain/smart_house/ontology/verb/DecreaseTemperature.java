package edu.cmu.sv.domain.smart_house.ontology.verb;

import edu.cmu.sv.domain.smart_house.ontology.role.HasRoom;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dan on 4/21/15.
 */
public class DecreaseTemperature extends Verb {
    static Set<Class <? extends Role>> requiredGroundedRoles = new HashSet<>();
    static {
        requiredGroundedRoles.add(HasRoom.class);
    }


    @Override
    public Set<Class<? extends Role>> getRequiredGroundedRoles() {
        return requiredGroundedRoles;
    }
}
