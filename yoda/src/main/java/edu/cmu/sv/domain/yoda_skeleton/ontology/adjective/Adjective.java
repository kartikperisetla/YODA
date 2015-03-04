package edu.cmu.sv.domain.yoda_skeleton.ontology.adjective;

import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class Adjective extends ThingWithRoles {


    public abstract double getCenter();
    public abstract double getSlope();
    public abstract Class<? extends TransientQuality> getQuality();
}
