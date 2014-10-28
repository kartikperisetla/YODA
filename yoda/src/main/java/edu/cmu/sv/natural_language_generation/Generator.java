package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.semantics.SemanticsModel;

import java.util.LinkedList;
import java.util.Set;

/**
 * YODA's built-in NLG module
 */
public class Generator {
    YodaEnvironment yodaEnvironment;

    public Generator(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    /*
    * The NLG function that is called by a dialog system at each turn.
    * This function may make use of various information in the yodaEnvironment
    * to select from the many possible expressions.
    * */
    public String generateBestForSemantics(SemanticsModel model){
        return new LinkedList<>(generateAll(model)).get(0);
    }

    // TODO: implement
    public Set<String> generateAll(SemanticsModel model){
        return null;
    }

}
