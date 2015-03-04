package edu.cmu.sv.domain.yelp_phoenix.ontology.adjective;

import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yelp_phoenix.ontology.quality.unary_quality.Goodness;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class GoodnessAdjective extends Adjective {

    @Override
    public Class<? extends TransientQuality> getQuality() {
        return Goodness.class;
    }
}
