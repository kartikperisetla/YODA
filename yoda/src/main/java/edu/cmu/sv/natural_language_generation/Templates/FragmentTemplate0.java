package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
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
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        Map<String, JSONObject> ans = new HashMap<>();
        if (SemanticsModel.anySLUTopLevelConflicts(new SemanticsModel(applicabilityConstraint),
                new SemanticsModel(constraints)))
            return ans;

        JSONObject topicWebResource = (JSONObject) new SemanticsModel(constraints).
                newGetSlotPathFiller("topic");

        // we just generate the noun phrase, no extra content
        yodaEnvironment.nlg.generateAll(topicWebResource, yodaEnvironment).
                entrySet().forEach(y -> ans.put(y.getKey(), y.getValue()));
        return ans;
    }
}
