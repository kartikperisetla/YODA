package edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2;

/**
 * Created by David Cohen on 9/20/14.
 */
public class Noun2{
    public String name;
    public Noun2 directParent;

    public Noun2(String name, Noun2 directParent) {
        this.name = name;
        this.directParent = directParent;
    }
}
