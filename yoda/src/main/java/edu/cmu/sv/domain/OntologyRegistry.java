package edu.cmu.sv.domain;

import edu.cmu.sv.domain.ontology2.*;

import java.util.Set;

/**
 * Created by David Cohen on 3/3/15.
 */
public interface OntologyRegistry {
    public Set<Verb2> getVerbs();
    public Set<Role2> getRoles();
    public Set<Noun2> getNouns();
    public Set<Quality2> getQualities();
    public Set<QualityDegree> getQualityDegrees();
}
