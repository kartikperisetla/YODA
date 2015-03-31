package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by David Cohen on 1/21/15.
 */
public class TimeFragmentInterpreter implements MiniLanguageInterpreter {
    TimeInterpreter timeInterpreter;

    public TimeFragmentInterpreter(TimeInterpreter timeInterpreter) {
        this.timeInterpreter = timeInterpreter;
    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        Pair<JSONObject, Double> timeInterpretation = ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).
                timeInterpreter.interpret(tokens, yodaEnvironment);
        if (timeInterpretation==null)
            return null;
        String jsonString = "{\"dialogAct\":\"Fragment\"}";
        JSONObject ans = SemanticsModel.parseJSON(jsonString);
        ans.put("topic", timeInterpretation.getLeft());
        return new ImmutablePair<>(ans, timeInterpretation.getRight());
    }
}
