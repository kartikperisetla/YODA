package edu.cmu.sv.ontology.adjective;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.role.Role;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class Adjective extends ThingWithRoles {


    public abstract double getCenter();
    public abstract double getSlope();
    public abstract Class<? extends TransientQuality> getQuality();
}
