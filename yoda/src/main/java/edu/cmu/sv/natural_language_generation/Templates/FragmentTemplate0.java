package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.natural_language_generation.GrammarRegistry;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/29/14.
 */
public class FragmentTemplate0 implements Template {
    static JSONObject applicabilityConstraint;
    static {
        try {
            applicabilityConstraint= (JSONObject) SemanticsModel.parser.
                    parse("{\"dialogAct\":\"" + Fragment.class.getSimpleName() + "\"}");
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                generateAll(topicWebResource, yodaEnvironment, GrammarRegistry.MAX_NP_DEPTH);

        for (String key : nounPhraseChunks.keySet()){
            JSONObject content = nounPhraseChunks.get(key);
            JSONObject newContent = SemanticsModel.
                    parseJSON("{\"dialogAct\":\"Fragment\",\"topic\":"+content.toJSONString()+"}");
            ans.put(key, newContent);
        }

        return ans;
    }
}
