package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.Set;

/**
 * Created by David Cohen on 1/21/15.
 */
public class SimpleStringMatchInterpreter implements MiniLanguageInterpreter {
    String jsonString;
    Set<String> matches;

    public SimpleStringMatchInterpreter(String jsonString, Set<String> matches) {
        this.jsonString = jsonString;
        this.matches = matches;
    }

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
        for (String match : matches){
            if (utterance.equals(match)) {
                return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), RegexPlusKeywordUnderstander.simpleStringMatchInterpreterWeight);
            }
        }
        return null;
    }
}
