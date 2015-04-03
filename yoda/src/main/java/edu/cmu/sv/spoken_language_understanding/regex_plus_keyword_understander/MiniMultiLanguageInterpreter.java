package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by David Cohen on 1/21/15.
 */
public interface MiniMultiLanguageInterpreter {
    // interpreting
    public NBestDistribution<JSONObject> interpret(List<String> tokens, YodaEnvironment yodaEnvironment);
}
