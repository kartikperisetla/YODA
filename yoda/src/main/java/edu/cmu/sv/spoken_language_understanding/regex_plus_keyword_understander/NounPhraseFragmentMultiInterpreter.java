package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by David Cohen on 4/23/15.
 */
public class NounPhraseFragmentMultiInterpreter implements MiniMultiLanguageInterpreter {
    NounPhraseInterpreter npInterpreter;

    public NounPhraseFragmentMultiInterpreter(NounPhraseInterpreter npInterpreter) {
        this.npInterpreter = npInterpreter;
    }

    @Override
    public NBestDistribution<JSONObject> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        Pair<JSONObject, Double> npInterpretation = npInterpreter.interpret(tokens, yodaEnvironment);
        NBestDistribution<JSONObject> ans = new NBestDistribution<>();
        String jsonString = "{\"dialogAct\":\"Fragment\"}";
        JSONObject tmp = SemanticsModel.parseJSON(jsonString);
        tmp.put("topic", npInterpretation.getLeft());
        ans.put(tmp, npInterpretation.getRight() * RegexPlusKeywordUnderstander.namedEntityFragmentWeight);
        return ans;
    }
}
