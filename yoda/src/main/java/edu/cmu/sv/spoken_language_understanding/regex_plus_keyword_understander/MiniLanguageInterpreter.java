package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 1/21/15.
 */
public interface MiniLanguageInterpreter {
    // interpreting
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment);
}
