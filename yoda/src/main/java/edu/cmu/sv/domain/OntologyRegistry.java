package edu.cmu.sv.domain;

import edu.cmu.sv.domain.ontology.*;

import java.util.Set;

/**
 * Created by David Cohen on 3/3/15.
 */
public interface OntologyRegistry {
    public Set<Verb> getVerbs();
    public Set<Role> getRoles();
    public Set<Noun> getNouns();
    public Set<Quality> getQualities();
    public Set<QualityDegree> getQualityDegrees();
}
