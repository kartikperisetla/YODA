package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts.RequestRoleGivenRole;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by David Cohen on 11/1/14.
 *
 * A special NLG template for requesting roles when it is the agent role that's requested
 *
 */
public class RequestAgentTemplate implements Template {

    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        SemanticsModel constraintsModel = new SemanticsModel(constraints);
        String verbClassString;
        JSONObject patientDescription;
        try{
            Assert.verify(constraints.get("dialogAct").equals(RequestRoleGivenRole.class.getSimpleName()));
            Assert.verify(constraints.containsKey("verb"));
            JSONObject verbObject = (JSONObject)constraints.get("verb");
            verbClassString = (String)verbObject.get("class");
            Assert.verify(verbClassString.equals(HasProperty.class.getSimpleName()));
            Assert.verify(constraintsModel.findAllPathsToClass(Requested.class.getSimpleName()).size()==1);
            Assert.verify(constraintsModel.newGetSlotPathFiller("verb.Agent.class").equals(Requested.class.getSimpleName()));
            Assert.verify(constraintsModel.newGetSlotPathFiller("verb.Patient")!=null);
            patientDescription = (JSONObject)verbObject.get("Patient");
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }

        Map<String, JSONObject> whChunks = new HashMap<>();
        whChunks.put("what", SemanticsModel.parseJSON("{}"));

        Map<String, JSONObject> verbChunks = new HashMap<>();
        Set<String> verbStrings;
        try{
            verbStrings = Lexicon.getPOSForClass(OntologyRegistry.thingNameMap.get(verbClassString),
                    Lexicon.LexicalEntry.PART_OF_SPEECH.S1_VERB, yodaEnvironment.nlg.grammarPreferences);
        } catch (Lexicon.NoLexiconEntryException e) {
            verbStrings = new HashSet<>();
        }
        for (String verbString : verbStrings) {
            verbChunks.put(verbString, SemanticsModel.parseJSON(constraints.toJSONString()));
        }

        Map<String, JSONObject> descriptionChunks = yodaEnvironment.nlg.
                generateAll(patientDescription, yodaEnvironment, yodaEnvironment.nlg.grammarPreferences.maxNounPhraseDepth);

//        System.out.println("RequestAgentTemplate:\nwhChunks:"+whChunks+"\nverbChunks:"+verbChunks+"\ndescriptionChunks:"+descriptionChunks);


        Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
        childNodeChunks.put("verb.Agent", new ImmutablePair<>(1,1));
        childNodeChunks.put("verb.Patient", new ImmutablePair<>(2,2));
        return GenerationUtils.simpleOrderedCombinations(Arrays.asList(verbChunks, whChunks, descriptionChunks),
                RequestAgentTemplate::compositionFunction, childNodeChunks, yodaEnvironment);
    }

    private static JSONObject compositionFunction(List<JSONObject> children) {
        JSONObject verbPhrase = children.get(0);
        JSONObject whPhrase = children.get(1);
        JSONObject descriptionPhrase = children.get(2);
        return SemanticsModel.parseJSON(verbPhrase.toJSONString());
    }
}
