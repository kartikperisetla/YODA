package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/29/14.
 */
public class FragmentTemplate0 implements Template {
    static JSONObject applicabilityConstraint;
    static {
        applicabilityConstraint = SemanticsModel.parseJSON("{\"dialogAct\":\"" + Fragment.class.getSimpleName() + "\"}");
    }

    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        Map<String, JSONObject> ans = new HashMap<>();
        if (SemanticsModel.anySLUTopLevelConflicts(new SemanticsModel(applicabilityConstraint),
                new SemanticsModel(constraints)))
            return ans;

        JSONObject topicWebResource = (JSONObject) new SemanticsModel(constraints).
                newGetSlotPathFiller("topic");

        // generate the noun phrase chunks
        Map<String, JSONObject> nounPhraseChunks = yodaEnvironment.nlg.
                generateAll(topicWebResource, yodaEnvironment, yodaEnvironment.nlg.grammarPreferences.maxNounPhraseDepth);

        for (String key : nounPhraseChunks.keySet()){
            JSONObject content = nounPhraseChunks.get(key);
            JSONObject newContent = SemanticsModel.
                    parseJSON("{\"dialogAct\":\"Fragment\",\"topic\":"+content.toJSONString()+"}");
            ans.put(key, newContent);
        }

        return ans;
    }
}
