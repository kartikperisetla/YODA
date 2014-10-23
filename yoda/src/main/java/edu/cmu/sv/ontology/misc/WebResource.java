package edu.cmu.sv.ontology.misc;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * WebResource is a wrapper class around semantic web resources.
 * Within the context of a YODA application, it contains the URI to a specific entity in the application's database.
 * This can represent a grounded reference, or a value such as a string or number extracted from an utterance
 *
 *
 * Created by David Cohen on 10/13/14.
 */
public class WebResource extends ThingWithRoles {
    @Override
    public Set<Class<? extends Role>> getRequiredRoles() {
        return new HashSet<>();
    }
}
