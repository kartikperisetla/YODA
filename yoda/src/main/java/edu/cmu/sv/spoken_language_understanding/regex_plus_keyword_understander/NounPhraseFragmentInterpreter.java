package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 1/21/15.
 */
public class NounPhraseFragmentInterpreter implements MiniLanguageInterpreter {
    NounPhraseInterpreter npInterpreter;

    public NounPhraseFragmentInterpreter(NounPhraseInterpreter npInterpreter) {
        this.npInterpreter = npInterpreter;
    }

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
        Pair<JSONObject, Double> npInterpretation = ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).
                nounPhraseInterpreter.interpret(utterance, yodaEnvironment);
//        System.err.println("NPFragment Interpreter: npInterpretation:" + npInterpretation.getLeft());
        if (npInterpretation.getKey().containsKey("HasName"))
            return null;
        String jsonString = "{\"dialogAct\":\"Fragment\"}";
        JSONObject ans = SemanticsModel.parseJSON(jsonString);
        ans.put("topic", npInterpretation.getLeft());
        return new ImmutablePair<>(ans, npInterpretation.getRight());
    }
}
