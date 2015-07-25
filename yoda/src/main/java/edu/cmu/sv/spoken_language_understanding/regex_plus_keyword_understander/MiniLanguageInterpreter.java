package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by David Cohen on 1/21/15.
 */
public interface MiniLanguageInterpreter {
    // interpreting
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment);
    public static final String possessivePrepositionRegexString = "(of|at|for|of|to|in|belonging to|by|)";
    public static final String startingPolitenessRegexString = "(please |could you |if you would |if you could |possibly |if it is possible |if it 's possible |would you |)";
    public static final String endingPolitenessRegexString = "(please|could you|if you would|if you could|possibly|if it is possible|if it 's possible|would you|ok|)+";
    public static final String ynqTagRegexString = "(right|is n't it|aren't they|isn't that right|no|are they|is it)";
    public static final String negationRegexString = "(not|no|n't)";
    public static final String putInStateVerbRegexString = "(turn|set|make|put|switch)";
    public static final String haveRegexString = "(have|has)";
    public static final String doRegexString = "(do|does)";
    public static final String whatString = "(what|which)";
}
