package edu.cmu.sv.ontology.adjective;

import edu.cmu.sv.ontology.quality.unary_quality.Height;
import edu.cmu.sv.ontology.quality.TransientQuality;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class AbsoluteHeightDegree extends AbsoluteQualityDegree{
    @Override
    public Class<? extends TransientQuality> getQuality() {
        return Height.class;
    }
}
