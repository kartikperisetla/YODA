package edu.cmu.sv.domain;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2.Noun2;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2.Quality2;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2.Role2;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;

import java.util.Set;

/**
 * Created by David Cohen on 3/3/15.
 */
public interface OntologyRegistry {
    Set<Class<? extends Verb>> getVerbClasses();
    Set<Class<? extends Noun>> getNounClasses();
    Set<Class<? extends Adjective>> getAdjectiveClasses();
    Set<Class<? extends Preposition>> getPrepositionClasses();
    Set<Class<? extends Quality2>> getQualityClasses();
    Set<Class<? extends Thing>> getMiscClasses();
    Set<Role2> getRoles();
    Set<Noun2> getNouns();
}
