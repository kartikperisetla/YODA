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
    public static String possessivePrepositionRegexString = "(of|at|for|of|to|in|belonging to|by|)";
    public static String startingPolitenessRegexString = "(please |could you |if you would |if you could |possibly |if it is possible |if it's possible |would you |)";
    public static String endingPolitenessRegexString = "( please| could you| if you would| if you could| possibly| if it is possible| if it's possible| would you| ok|)";
    public static String ynqTagRegexString = "(right|isn't it|aren't they|isn't that right|no|are they|is it)";
}
