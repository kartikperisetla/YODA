package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Command;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 11/1/14.
 */
public class CommandTemplate0 implements Template {
    static JSONObject applicabilityConstraint;
    static {
        applicabilityConstraint= (JSONObject) SemanticsModel.parseJSON("{\"dialogAct\":\"" + Command.class.getSimpleName() + "\"}");
    }

    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        Map<String, JSONObject> ans = new HashMap<>();
        if (SemanticsModel.anySLUTopLevelConflicts(new SemanticsModel(applicabilityConstraint),
                new SemanticsModel(constraints)))
            return ans;

        JSONObject topicWebResource = (JSONObject) new SemanticsModel(constraints).
                newGetSlotPathFiller("topic");


        JSONObject dialogActFrame = SemanticsModel.
                parseJSON("{\"dialogAct\":\"Command\",\"verb\":{\"class\":\"GiveDirections\",\"Patient\":{}}}");
        Map<String, JSONObject> commandStringChunks = new HashMap<>();
        for (String ex : Arrays.asList("give me directions to", "directions please", "please give directions to")){
            commandStringChunks.put(ex, dialogActFrame);
        }

        // generate the noun phrase chunks
        Map<String, JSONObject> nounPhraseChunks = yodaEnvironment.nlg.
                generateAll(topicWebResource, yodaEnvironment, yodaEnvironment.nlg.grammarPreferences.maxNounPhraseDepth);

        Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
        childNodeChunks.put("verb.Patient", new ImmutablePair<>(1,1));

        return GenerationUtils.simpleOrderedCombinations(Arrays.asList(commandStringChunks, nounPhraseChunks),
                CommandTemplate0::compositionFunction, childNodeChunks, yodaEnvironment);
    }

    private static JSONObject compositionFunction(List<JSONObject> children) {
        JSONObject command = children.get(0);
        JSONObject nounPhrase = children.get(1);
        SemanticsModel ans = new SemanticsModel(command.toJSONString());
        ans.extendAndOverwriteAtPoint("verb.Patient", new SemanticsModel(nounPhrase.toJSONString()));
        return ans.getInternalRepresentation();
    }
}
