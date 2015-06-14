package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 6/13/14.
 *
 * Unlike top-level templates, phrase generation routines have their constraints passed to their constructors.
 */
public interface PhraseGenerationRoutine {
    ImmutablePair<String, JSONObject> generate(JSONObject constraints, YodaEnvironment yodaEnvironment);

}
