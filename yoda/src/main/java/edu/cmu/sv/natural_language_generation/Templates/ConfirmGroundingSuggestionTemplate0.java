package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.ConfirmValueSuggestion;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/29/14.
 */
public class ConfirmGroundingSuggestionTemplate0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // ensure that the constraints match this template
        try {
            assert constraints.get("dialogAct").equals(ConfirmValueSuggestion.class.getSimpleName());
            assert constraints.keySet().size()==1;
        } catch (AssertionError e){
            return new HashMap<>();
        }
        Map<String, JSONObject> ans = new HashMap<>();
        ans.put("Yes", SemanticsModel.parseJSON(constraints.toJSONString()));
        return ans;
    }
}
