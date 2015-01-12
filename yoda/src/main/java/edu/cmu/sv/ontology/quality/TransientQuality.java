package edu.cmu.sv.ontology.quality;


import edu.cmu.sv.ontology.Thing;

import java.util.List;

/**
 * Created by David Cohen on 10/31/14.
 */
public abstract class TransientQuality extends Thing{
   /*
    * Return a query fragment that binds the transient quality to ?transient_quality
    * (based on some non-transient information)
    *
    * The transient quality must be in the range (0-1) if it can be determined.
    *
    * DO NOT USE THE FOLLOWING VARIABLES:
    * ?x
    * ?fuzzy_mapped_quality
    *
    * The input list should be able to contain either variables: ?XXX or URIs: <http://sdfs.sdfsdf.sdfsdf#lkjssdf>
    * */
    public abstract java.util.function.Function<List<String>, String> getQualityCalculatorSPARQLQuery();


    /*
    * Return a sorted list of the classes of the arguments to the quality calculator,
    * other than the subject
    * For adjectives, this will be an empty list.
    * */
    public abstract List<Class<? extends Thing>> getArguments();
}
