package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by David Cohen on 6/13/14.
 *
 * Interface for top level NLG2 template
 *
 */
public interface TopLevelNLGTemplate {
    /*
    * Constraints are passed from above,
    * and the template generates a string /
    * completed / elaborated constraints to pass back as output
    * */
    ImmutablePair<String, SemanticsModel> generate(SemanticsModel constraints, YodaEnvironment yodaEnvironment);

}
