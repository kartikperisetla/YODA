package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;


import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 1/27/15.
 */
public class TimeInterpreter implements MiniLanguageInterpreter{
    Map<String, Long> hourMap = new HashMap<String, Long>() {{
        put("one", (long)1);
        put("two", (long)2);
        put("three", (long)3);
        put("four", (long)4);
        put("five", (long)5);
        put("six", (long)6);
        put("seven", (long)7);
        put("eight", (long)8);
        put("nine", (long)9);
        put("ten", (long)10);
        put("eleven", (long)11);
        put("twelve", (long)0);
        put("1", (long)1);
        put("2", (long)2);
        put("3", (long)3);
        put("4", (long)4);
        put("5", (long)5);
        put("6", (long)6);
        put("7", (long)7);
        put("8", (long)8);
        put("9", (long)9);
        put("10", (long)10);
        put("11", (long)11);
        put("12", (long)0);
    }};
    Map<String, Long> tenMinuteMap = new HashMap<String, Long>() {{
        // TODO teens are complicated
        put("twenty", (long)2);
        put("thirty", (long)3);
        put("forty", (long)4);
        put("fifty", (long)5);
    }};
    Map<String, Long> singleMinuteMap = new HashMap<String, Long>() {{
        put("one", (long)1);
        put("two", (long)2);
        put("three", (long)3);
        put("four", (long)4);
        put("five", (long)5);
        put("six", (long)6);
        put("seven", (long)7);
        put("eight", (long)8);
        put("nine", (long)9);
        put("1", (long)1);
        put("2", (long)2);
        put("3", (long)3);
        put("4", (long)4);
        put("5", (long)5);
        put("6", (long)6);
        put("7", (long)7);
        put("8", (long)8);
        put("9", (long)9);
    }};
    Map<String, String> amPmMap = new HashMap<>();

    YodaEnvironment yodaEnvironment;

    public TimeInterpreter(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;

        singleMinuteMap.put("five", (long) 5);
        singleMinuteMap.put("5", (long) 5);

        amPmMap.put("a.m.", "AM");
        amPmMap.put("am", "AM");
        amPmMap.put("p.m.", "PM");
        amPmMap.put("pm", "PM");
        amPmMap.put("morning", "AM");
        amPmMap.put("afternoon", "PM");
        amPmMap.put("evening", "PM");
    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        JSONObject ans = new JSONObject();
        ans.put("class", YodaSkeletonOntologyRegistry.timeNounClass.name);
        List<String> remainingTokens = new LinkedList<>(tokens);
        for (String token : remainingTokens){
            if (hourMap.containsKey(token)){
                ans.put(YodaSkeletonOntologyRegistry.hasHour.name, hourMap.get(token));
                remainingTokens.remove(token);
                break;
            }
        }

        if (ans.containsKey(YodaSkeletonOntologyRegistry.hasHour.name)) {
            for (String token : remainingTokens) {
                if (tenMinuteMap.containsKey(token)) {
                    ans.put(YodaSkeletonOntologyRegistry.hasTenMinute.name, tenMinuteMap.get(token));
                    remainingTokens.remove(token);
                    break;
                }
            }

            for (String token : remainingTokens) {
                if (singleMinuteMap.containsKey(token)) {
                    ans.put(YodaSkeletonOntologyRegistry.hasSingleMinute.name, singleMinuteMap.get(token));
                    remainingTokens.remove(token);
                    break;
                }
            }
        }

        for (String token : remainingTokens){
            if (amPmMap.containsKey(token)){
                ans.put(YodaSkeletonOntologyRegistry.hasAmPm.name, amPmMap.get(token));
                remainingTokens.remove(token);
                break;
            }
        }

//        System.err.println("TimeInterpreter: final result:" + ans);

        if (ans.keySet().size()>1) {
            return new ImmutablePair<>(ans, RegexPlusKeywordUnderstander.timeInterpreterWeight);
        }
        return null;
    }
}
