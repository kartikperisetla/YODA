package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.Tokenizer;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 1/21/15.
 */
public class WhqExistRegexInterpreter implements MiniLanguageInterpreter {
    static String whqExistsPrefixRegexString = "(are any |are there |is there |i 'm looking for |i want |find |get |search for |show me |look for |search |look |show )";

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        String utterance = String.join(" ", tokens);
        Pattern regexPattern = Pattern.compile(startingPolitenessRegexString + whqExistsPrefixRegexString + "(.+?)" + endingPolitenessRegexString);
        Matcher matcher = regexPattern.matcher(utterance);
        double matchQuality = utterance.startsWith("i want ") || utterance.startsWith("get ") ?
                RegexPlusKeywordUnderstander.secondaryRegexMatchWeight :
                RegexPlusKeywordUnderstander.regexInterpreterWeight;
        if (matcher.matches()) {
            String npString = matcher.group(3);
            Pair<JSONObject, Double> npInterpretation =
                    ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);
            if (npInterpretation.getKey().containsKey("HasName"))
                return null;
            String jsonString = "{\"dialogAct\":\"WHQuestion\",\"verb\":{\"Agent\":" +
                    npInterpretation.getKey().toJSONString() + ",\"class\":\"Exist\"}}";
            return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), matchQuality);
        }

        //// Inverse role patterns
        {
            String nestedPhraseString = null;
            String roleString = null;
            // "what X does Y have?"
            Pattern regexPattern3 = Pattern.compile(startingPolitenessRegexString + whatString + " (.+?)" + doRegexString + " (.+?)" + haveRegexString + ".*" + endingPolitenessRegexString);
            Matcher matcher3 = regexPattern3.matcher(utterance);

            // "does y have any x?"
            Pattern regexPattern4 = Pattern.compile(startingPolitenessRegexString + doRegexString + " (.+?)" + haveRegexString+ " (.+?)" + endingPolitenessRegexString);
            Matcher matcher4 = regexPattern4.matcher(utterance);

            // "what are y's xes?"
            Pattern regexPattern5 = Pattern.compile(startingPolitenessRegexString + whatString + " (is|are) (.+?) ('s) (.+?)" + endingPolitenessRegexString);
            Matcher matcher5 = regexPattern5.matcher(utterance);


            if (matcher3.matches()) {
//                System.err.println("WhqExistRegexInterpreter: here1");
                roleString = matcher3.group(3);
                nestedPhraseString = matcher3.group(5);
            } else if (matcher4.matches()){
//                System.err.println("WhqExistRegexInterpreter: here2");
                roleString = matcher4.group(5);
                nestedPhraseString = matcher4.group(3);
//                System.err.println("role string:" + roleString);
//                System.err.println("nested phrase string:" + nestedPhraseString);
            } else if (matcher5.matches()){
//                System.err.println("WhqExistRegexInterpreter: here3");
                roleString = matcher5.group(6);
                nestedPhraseString = matcher5.group(4);
            }
//            System.err.println("WhqExistRegexInterpreter: here4");

            if (nestedPhraseString!=null){
                NBestDistribution<JSONObject> interpretedInverseRelations =
                        ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).relationInterpreter.interpret(
                                Tokenizer.tokenize(roleString), Tokenizer.tokenize(nestedPhraseString));
                if (interpretedInverseRelations.internalDistribution.size()>0) {
                    JSONObject ans = SemanticsModel.parseJSON("{\"dialogAct\":\"WHQuestion\",\"verb\":{\"class\":\"Exist\", \"Agent\":" +
                            interpretedInverseRelations.getTopHypothesis().toJSONString() + "}}");
                    return new ImmutablePair<>(ans, interpretedInverseRelations.get(interpretedInverseRelations.getTopHypothesis()));
                }
            }
        }

        Pattern regexPattern2 = Pattern.compile(startingPolitenessRegexString + "(.+?)" + endingPolitenessRegexString);
        Matcher matcher2 = regexPattern2.matcher(utterance);
        if (matcher2.matches()) {
            String npString = matcher2.group(2);
            Pair<JSONObject, Double> npInterpretation =
                    ((RegexPlusKeywordUnderstander)yodaEnvironment.slu).nounPhraseInterpreter.interpret(Tokenizer.tokenize(npString), yodaEnvironment);
            if (npInterpretation.getKey().containsKey("HasName"))
                return null;
            String jsonString = "{\"dialogAct\":\"WHQuestion\",\"verb\":{\"Agent\":" +
                    npInterpretation.getKey().toJSONString() + ",\"class\":\"Exist\"}}";
            return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), RegexPlusKeywordUnderstander.secondaryRegexMatchWeight);
        }

        //todo: "what X are there?"
        return null;
    }
}
