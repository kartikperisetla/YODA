package edu.cmu.sv.ontology.adjective;

import edu.cmu.sv.ontology.quality.unary_quality.Expensiveness;
import edu.cmu.sv.ontology.quality.TransientQuality;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class ExpensivenessAdjective extends Adjective {

    @Override
    public Class<? extends TransientQuality> getQuality() {
        return Expensiveness.class;
    }
}
