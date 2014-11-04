package edu.cmu.sv.ontology.adjective;

import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.quality.TransientQuality;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class AbsoluteQualityDegree extends ThingWithRoles {
    public abstract double getCenter();
    public abstract double getSlope();
    public abstract Class<? extends TransientQuality> getQuality();
}
