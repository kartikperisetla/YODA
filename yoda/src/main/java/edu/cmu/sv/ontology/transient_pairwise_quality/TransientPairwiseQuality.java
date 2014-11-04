package edu.cmu.sv.ontology.transient_pairwise_quality;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.role.Role;
import org.openrdf.query.algebra.evaluation.function.Function;

/**
 * Created by David Cohen on 11/3/14.
 *
 * A TransientPairwiseRole is a specific degree of a TransientPairwiseQuality
 * Ex: IsCloseTo (pairwise role) is a specific degree of Distance (pairwise quality)
 *
 * A TransientPairwiseQuality is transient because it is not permanently stored in the system's long-term memory
 * but computed as-needed to determine the applicability of TransitiveRoles
 *
 * Q: Why does transient-quality extend role, but transient-role extends thing?
 * A: Because domain and range constraints
 *
 */
public abstract class TransientPairwiseQuality extends Role {

    /*
    * Return the function that calculates the transient quality at a particular time
    * (based on some non-transient information)
    * */
    public abstract Class<? extends Function> getQualityCalculatorSPARQLFunctionClass();

}
