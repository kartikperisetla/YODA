package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasName;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/29/14.
 */
public class AcceptTemplate implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // ensure that the constraints match this template
        try {
            assert constraints.get("dialogAct").equals(Accept.class.getSimpleName());
            assert constraints.keySet().size()==1;
        } catch (AssertionError e){
            return new HashMap<>();
        }

        Map<String, JSONObject> ans = new HashMap<>();
        ans.put("Yes", SemanticsModel.parseJSON(constraints.toJSONString()));
        return ans;
    }
}
