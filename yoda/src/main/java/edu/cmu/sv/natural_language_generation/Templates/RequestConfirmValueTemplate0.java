package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.preposition.Preposition;
import edu.cmu.sv.ontology.role.Agent;
import edu.cmu.sv.ontology.role.InRelationTo;
import edu.cmu.sv.ontology.role.Patient;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.YNQuestion;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 10/29/14.
 */
public class RequestConfirmValueTemplate0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        JSONObject topicWebResource;
        // ensure that the constraints match this template
        try {
            assert constraints.get("dialogAct").equals(RequestConfirmValue.class.getSimpleName());
            assert constraints.keySet().size()==2;
            assert constraints.containsKey("topic");
            topicWebResource = (JSONObject) constraints.get("topic");
        } catch (AssertionError e){
            return new HashMap<>();
        }

        Map<String, JSONObject> beginningChunks = new HashMap<>();
        Map<String, JSONObject> nounPhraseChunks = yodaEnvironment.nlg.
                generateAll(topicWebResource, yodaEnvironment, yodaEnvironment.nlg.grammarPreferences.maxNounPhraseDepth);
        Map<String, JSONObject> endingChunks = new HashMap<>();
        beginningChunks.put("you mean", new JSONObject());
        endingChunks.put("is that right", new JSONObject());

        Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
        childNodeChunks.put("topic", new ImmutablePair<>(1,1));
        return GenerationUtils.simpleOrderedCombinations(Arrays.asList(beginningChunks, nounPhraseChunks, endingChunks),
                RequestConfirmValueTemplate0::compositionFunction, childNodeChunks, yodaEnvironment);
    }

    private static JSONObject compositionFunction(List<JSONObject> children){
        JSONObject beginning = children.get(0);
        JSONObject child = children.get(1);
        JSONObject end = children.get(2);

        SemanticsModel ans = new SemanticsModel("{\"dialogAct\":\""+RequestConfirmValue.class.getSimpleName()+
                "\", \"topic\": "+child.toJSONString()+"}");

        return ans.getInternalRepresentation();
    }

}
