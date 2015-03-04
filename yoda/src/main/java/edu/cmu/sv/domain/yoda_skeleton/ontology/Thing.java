package edu.cmu.sv.domain.yoda_skeleton.ontology;


import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 9/23/14.
 */
public abstract class Thing {
    // for a thing to be semantically complete,
    // certain role information may be required
    // particularly, grounded roles and descriptions
    public Set<Class <? extends Role>> getRequiredGroundedRoles(){return new HashSet<>();}
    public Set<Class <? extends Role>> getRequiredDescriptions(){return new HashSet<>();}


}
