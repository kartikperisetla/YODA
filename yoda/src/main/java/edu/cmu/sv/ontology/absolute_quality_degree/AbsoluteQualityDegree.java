package edu.cmu.sv.ontology.absolute_quality_degree;

import edu.cmu.sv.ontology.ThingWithRoles;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class AbsoluteQualityDegree extends ThingWithRoles {
    public abstract double getCenter();
    public abstract double getSlope();
}
