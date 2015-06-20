package edu.cmu.sv.domain.ontology;

/**
 * Created by David Cohen on 9/20/14.
 */
public class Noun {
    public String name;
    public Noun directParent;

    public Noun(String name, Noun directParent) {
        this.name = name;
        this.directParent = directParent;
    }
}
