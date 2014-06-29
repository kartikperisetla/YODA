package edu.cmu.sv.agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cohend on 6/29/14.
 */
public class SLUComponent {

    public Map<String, String> matchInput(String inputString){
        Map<String, String> ans = new HashMap<String, String>();
        Pattern queryPattern = Pattern.compile("describe (?<name>.+)");
        Matcher matcher = queryPattern.matcher(inputString);
        if (matcher.matches()) {
            ans.put("dialog_act","query_description");
            ans.put("entity_name", matcher.group("name"));
            return ans;
        }
        Pattern insertPattern = Pattern.compile("create a (?<class>\\S+)( whose (?<slot>\\S+) is (?<value>\\S+))?");
        matcher = insertPattern.matcher(inputString);
        if (matcher.matches()) {
            ans.put("dialog_act", "insert_description");
            ans.put("class", matcher.group("class"));
            try {
                ans.put("slot", matcher.group("slot"));
                ans.put("value", matcher.group("value"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ans;
        }
        if (inputString.equals("ok")) {
            ans.put("dialog_act", "confirmation");
            return ans;
        }
        return ans;
    }

}
