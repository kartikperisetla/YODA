package edu.cmu.sv.ontology.transient_pairwise_quality;

import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.ThingWithRoles;
import edu.cmu.sv.ontology.object.PointOfInterest;
import edu.cmu.sv.ontology.transient_pairwise_role.TransientPairwiseRole;
import org.openrdf.query.algebra.evaluation.function.Function;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 11/3/14.
 */
public class HasDistance extends TransientPairwiseQuality {
    static Set<Class <? extends ThingWithRoles>> domain = new HashSet<>(Arrays.asList(PointOfInterest.class));
    static Set<Class <? extends Thing>> range = new HashSet<>(Arrays.asList(PointOfInterest.class));

    @Override
    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    @Override
    public Set<Class<? extends Thing>> getRange() {
        return range;
    }

    @Override
    public Class<? extends Function> getQualityCalculatorSPARQLFunctionClass() {
        return null;
    }
}
