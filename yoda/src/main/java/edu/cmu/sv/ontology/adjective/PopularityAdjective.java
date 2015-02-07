package edu.cmu.sv.ontology.adjective;

import edu.cmu.sv.ontology.quality.TransientQuality;
import edu.cmu.sv.ontology.quality.unary_quality.Popularity;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class PopularityAdjective extends Adjective {

    @Override
    public Class<? extends TransientQuality> getQuality() {
        return Popularity.class;
    }
}
