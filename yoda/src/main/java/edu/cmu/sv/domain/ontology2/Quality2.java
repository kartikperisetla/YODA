package edu.cmu.sv.domain.ontology2;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/31/14.
 */
public class Quality2{
    public String name;
    public Noun2 firstArgumentClassConstraint;
    public Noun2 secondArgumentClassConstraint;
    public QueryFragment queryFragment;
    Set<QualityDegree> qualityDegrees = new HashSet<>();

    public Quality2(String name, Noun2 firstArgumentClassConstraint, Noun2 secondArgumentClassConstraint, QueryFragment queryFragment) {
        this.name = name;
        this.firstArgumentClassConstraint = firstArgumentClassConstraint;
        this.secondArgumentClassConstraint = secondArgumentClassConstraint;
        this.queryFragment = queryFragment;
    }

    public Set<QualityDegree> getQualityDegrees(){return qualityDegrees;}

    //TODO: getMetric(), getUnitOfMeasurement()

}
