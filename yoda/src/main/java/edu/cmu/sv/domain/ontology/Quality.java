package edu.cmu.sv.domain.ontology;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/31/14.
 */
public class Quality {
    public String name;
    public Noun firstArgumentClassConstraint;
    public Noun secondArgumentClassConstraint;
    public QueryFragment queryFragment;
    Set<QualityDegree> qualityDegrees = new HashSet<>();

    public Quality(String name, Noun firstArgumentClassConstraint, Noun secondArgumentClassConstraint, QueryFragment queryFragment) {
        this.name = name;
        this.firstArgumentClassConstraint = firstArgumentClassConstraint;
        this.secondArgumentClassConstraint = secondArgumentClassConstraint;
        this.queryFragment = queryFragment;
    }

    public Set<QualityDegree> getQualityDegrees(){return qualityDegrees;}

    //TODO: getMetric(), getUnitOfMeasurement()

}
