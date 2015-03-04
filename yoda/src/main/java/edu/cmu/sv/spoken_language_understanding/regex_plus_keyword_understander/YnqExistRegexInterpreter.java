package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 1/21/15.
 */
public class YnqExistRegexInterpreter implements MiniLanguageInterpreter {
    static String ynqExistsPrefixRegexString = "(are there |i'm looking for |i want |find |get |search for |show me |look for |search |look |show )";

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
        Pattern regexPattern = Pattern.compile(startingPolitenessRegexString + ynqExistsPrefixRegexString + "(.+)" + endingPolitenessRegexString);
        Matcher matcher = regexPattern.matcher(utterance);
        double matchQuality = utterance.startsWith("i want ") || utterance.startsWith("get ") ? 0.3 : 1.0;
        if (matcher.matches()) {
            String npString = matcher.group(3);
            Pair<JSONObject, Double> npInterpretation =
                    ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(npString, yodaEnvironment);
            if (npInterpretation.getKey().containsKey("HasName"))
                return null;
            String jsonString = "{\"dialogAct\":\"YNQuestion\",\"verb\":{\"Agent\":" +
                    npInterpretation.getKey().toJSONString() + ",\"class\":\"Exist\"}}";
            return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), matchQuality);
        }

        Pattern regexPattern2 = Pattern.compile(startingPolitenessRegexString + "(.+)" + endingPolitenessRegexString);
        Matcher matcher2 = regexPattern2.matcher(utterance);
        if (matcher2.matches()) {
            String npString = matcher2.group(2);
            Pair<JSONObject, Double> npInterpretation =
                    ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(npString, yodaEnvironment);
            if (npInterpretation.getKey().containsKey("HasName"))
                return null;
            String jsonString = "{\"dialogAct\":\"YNQuestion\",\"verb\":{\"Agent\":" +
                    npInterpretation.getKey().toJSONString() + ",\"class\":\"Exist\"}}";
            return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), .3);
        }

        return null;
    }
}
