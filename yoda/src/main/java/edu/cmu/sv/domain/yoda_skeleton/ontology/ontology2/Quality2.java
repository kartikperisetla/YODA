package edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2;


import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;

import java.util.Set;

/**
 * Created by David Cohen on 10/31/14.
 */
public interface Quality2{
    Class<? extends Noun> getFirstArgumentClassConstraint();
    Class<? extends Noun> getSecondArgumentClassConstraint();
    Set<Class<? extends QualityDegree>> getQualityDegrees();
    String getSparqlQueryFragment(String firstArgument, String secondArgument, String resultVariable);
    //TODO: getMetric(), getUnitOfMeasurement()


//   /*
//    * Return a query fragment that binds the transient quality to ?transient_quality
//    * (based on some non-transient information)
//    *
//    * The transient quality must be in the range (0-1) if it can be determined.
//    *
//    * DO NOT USE THE FOLLOWING VARIABLES:
//    * ?x
//    * ?fuzzy_mapped_quality
//    *
//    * The input list should be able to contain either variables: ?XXX or URIs: <http://sdfs.sdfsdf.sdfsdf#lkjssdf>
//    * */
//    java.util.function.Function<List<String>, String> getQualityCalculatorSPARQLQuery();
}
