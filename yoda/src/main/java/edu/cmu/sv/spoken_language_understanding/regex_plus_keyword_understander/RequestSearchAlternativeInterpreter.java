package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.RequestSearchAlternative;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by David Cohen on 5/8/15.
 */

public class RequestSearchAlternativeInterpreter implements MiniLanguageInterpreter {
    static Set<String> keywords = new HashSet<>();
    static {
        keywords.add("other");
        keywords.add("alternative");
        keywords.add("else");
        keywords.add("another");
    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        for (String token : tokens){
            if (keywords.contains(token)){
                JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"" + RequestSearchAlternative.class.getSimpleName() + "\"}");
                return new ImmutablePair<>(ans, RegexPlusKeywordUnderstander.outOfCapabilityWeight);
            }
        }
        return null;
    }
}
