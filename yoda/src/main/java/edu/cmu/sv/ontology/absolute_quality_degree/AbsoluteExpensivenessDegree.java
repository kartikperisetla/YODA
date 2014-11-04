package edu.cmu.sv.ontology.absolute_quality_degree;

import edu.cmu.sv.ontology.quality.Expensiveness;
import edu.cmu.sv.ontology.quality.Quality;

/**
 * Created by David Cohen on 11/2/14.
 */
public abstract class AbsoluteExpensivenessDegree extends AbsoluteQualityDegree{

    @Override
    public Class<? extends Quality> getQuality() {
        return Expensiveness.class;
    }
}
