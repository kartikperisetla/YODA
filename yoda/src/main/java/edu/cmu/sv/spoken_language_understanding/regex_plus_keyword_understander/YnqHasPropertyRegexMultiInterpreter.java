package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.NBestDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by David Cohen on 1/21/15.
 */
public class YnqHasPropertyRegexMultiInterpreter implements MiniMultiLanguageInterpreter {
    YodaEnvironment yodaEnvironment;

    public YnqHasPropertyRegexMultiInterpreter(YodaEnvironment yodaEnvironment) {}

    @Override
    public NBestDistribution<JSONObject> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        NBestDistribution<JSONObject> ans = new NBestDistribution<>();
        if (tokens.size() < 3)
            return ans;

        if (!tokens.get(0).equals("is") && !tokens.get(0).equals("are"))
            return ans;

        if (tokens.get(1).equals("there") || tokens.get(1).equals("any") || tokens.get(1).equals("some"))
            return ans;

        int minNumberNonNamedEntityInterpretations = 2;
        for (int splitIndex = 2; splitIndex < tokens.size(); splitIndex++) {
            List<String> np1Tokens = tokens.subList(1, splitIndex);
            List<String> np2Tokens = tokens.subList(splitIndex, tokens.size());
//            System.err.println("Trying split:" + np1Tokens + np2Tokens);
            Pair<JSONObject, Double> npInterpretation1 = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                    nounPhraseInterpreter.interpret(np1Tokens, yodaEnvironment);
            Pair<JSONObject, Double> npInterpretation2 = ((RegexPlusKeywordUnderstander) yodaEnvironment.slu).
                    nounPhraseInterpreter.interpret(np2Tokens, yodaEnvironment);
            if (npInterpretation1 == null || npInterpretation2 == null)
                continue;
            double currentScore = npInterpretation1.getRight() * npInterpretation2.getRight();
//            System.err.println("score:" + currentScore);
            if (currentScore > 0.000001) {
                JSONObject hyp = SemanticsModel.parseJSON("{\"dialogAct\":\"YNQuestion\", \"verb\":{\"class\":\"HasProperty\"}}");
                SemanticsModel.putAtPath(hyp, "verb.Agent", npInterpretation1.getLeft());
                SemanticsModel.putAtPath(hyp, "verb.Patient", npInterpretation2.getLeft());
                ans.put(hyp, currentScore);
                minNumberNonNamedEntityInterpretations =
                        Integer.min(minNumberNonNamedEntityInterpretations, containsNamedEntityAgentOrPatient(hyp));
            }
        }

        // penalize interpretations with more named entities than the minimum interpretation
        for (JSONObject key : ans.keySet()) {
            ans.put(key, ans.get(key) *
                    Math.pow(RegexPlusKeywordUnderstander.namedEntityPenalty,
                            containsNamedEntityAgentOrPatient(key) - minNumberNonNamedEntityInterpretations));
        }
//        System.err.println("ans"+ans);
        return ans;
    }

    int containsNamedEntityAgentOrPatient(JSONObject interpretation){
        int ans = 0;
        ans += new SemanticsModel(interpretation).newGetSlotPathFiller("verb.Agent." + YodaSkeletonOntologyRegistry.hasName.name)!=null ? 1 : 0;
        ans += new SemanticsModel(interpretation).newGetSlotPathFiller("verb.Patient."+ YodaSkeletonOntologyRegistry.hasName.name)!=null ? 1 : 0;
        return ans;
    }
}
