package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Time;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasAmPm;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasHour;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasSingleMinute;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasTenMinute;
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
    Map<String, Long> hourMap = new HashMap<>();
    Map<String, Long> tenMinuteMap = new HashMap<>();
    Map<String, Long> singleMinuteMap = new HashMap<>();
    Map<String, String> amPmMap = new HashMap<>();

    YodaEnvironment yodaEnvironment;

    public TimeInterpreter(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
        hourMap.put("two", (long) 2);
        hourMap.put("2", (long) 2);

        tenMinuteMap.put("thirty", (long) 3);

        singleMinuteMap.put("five", (long) 5);
        singleMinuteMap.put("5", (long) 5);

        amPmMap.put("a.m.", "AM");
        amPmMap.put("p.m.", "PM");
        amPmMap.put("PM", "PM");
        amPmMap.put("P_ M_", "PM");
        amPmMap.put("afternoon", "PM");
        amPmMap.put("evening", "PM");
    }

    @Override
    public Pair<JSONObject, Double> interpret(List<String> tokens, YodaEnvironment yodaEnvironment) {
        JSONObject ans = new JSONObject();
        ans.put("class", Time.class.getSimpleName());
        List<String> remainingTokens = new LinkedList<>(tokens);
        for (String token : remainingTokens){
            if (hourMap.containsKey(token)){
                ans.put(HasHour.class.getSimpleName(), hourMap.get(token));
                remainingTokens.remove(token);
                break;
            }
        }

        if (ans.containsKey(HasHour.class.getSimpleName())) {
            for (String token : remainingTokens) {
                if (tenMinuteMap.containsKey(token)) {
                    ans.put(HasTenMinute.class.getSimpleName(), tenMinuteMap.get(token));
                    remainingTokens.remove(token);
                    break;
                }
            }

            for (String token : remainingTokens) {
                if (singleMinuteMap.containsKey(token)) {
                    ans.put(HasSingleMinute.class.getSimpleName(), singleMinuteMap.get(token));
                    remainingTokens.remove(token);
                    break;
                }
            }
        }

        for (String token : remainingTokens){
            if (amPmMap.containsKey(token)){
                ans.put(HasAmPm.class.getSimpleName(), amPmMap.get(token));
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
