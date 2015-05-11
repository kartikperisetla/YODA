package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.RequestListOptions;
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

public class RequestListOptionsInterpreter implements MiniLanguageInterpreter {
    static Set<String> keywords = new HashSet<>();
    static {
        keywords.add("list");
        keywords.add("all");
        keywords.add("options");
        keywords.add("alternatives");
        keywords.add("others");
    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        for (String token : tokens){
            if (keywords.contains(token)){
                JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"" + RequestListOptions.class.getSimpleName() + "\"}");
                return new ImmutablePair<>(ans, RegexPlusKeywordUnderstander.outOfCapabilityWeight);
            }
        }
        return null;
    }
}
