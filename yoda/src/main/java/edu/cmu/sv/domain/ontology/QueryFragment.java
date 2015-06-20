package edu.cmu.sv.domain.ontology;

/**
 * Created by David Cohen on 6/16/15.
 */
public interface QueryFragment {
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
    String getSparqlQueryFragment(String firstArgument, String secondArgument, String resultVariable);
}
