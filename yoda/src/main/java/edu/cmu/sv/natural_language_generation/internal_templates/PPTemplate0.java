package edu.cmu.sv.natural_language_generation.internal_templates;

import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;

import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.preposition.Preposition;
import edu.cmu.sv.ontology.role.InRelationTo;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 11/13/14.
 */
public class PPTemplate0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        String hasQualityRole;
        String prepositionClassString;
        String child;
        // ensure that the constraints match this template
        try {
            Assert.verify(constraints.get("class").equals(UnknownThingWithRoles.class.getSimpleName()));
            Assert.verify(constraints.keySet().size()==2);
            List<String> keys = (List<String>) constraints.keySet().stream().map(x -> (String)x).collect(Collectors.toList());
            keys.remove("class");
            hasQualityRole = keys.get(0);
            JSONObject prepositionContent = (JSONObject) constraints.get(hasQualityRole);
            Assert.verify(prepositionContent.keySet().size()==2);
            Assert.verify(prepositionContent.containsKey("class"));
            Assert.verify(prepositionContent.containsKey(InRelationTo.class.getSimpleName()));
            prepositionClassString = (String) prepositionContent.get("class");
            child = ((JSONObject) prepositionContent.get(InRelationTo.class.getSimpleName())).toJSONString();
            Assert.verify(SemanticsModel.parseJSON(child).containsKey("class"));
            Assert.verify(SemanticsModel.parseJSON(child).get("class").equals(WebResource.class.getSimpleName()));
            Assert.verify(OntologyRegistry.thingNameMap.containsKey(prepositionClassString));
            Assert.verify(Preposition.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(prepositionClassString)));
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }

        Map<String, JSONObject> prepositionChunks = new HashMap<>();
        Class<? extends Thing> prepositionClass = OntologyRegistry.thingNameMap.get(prepositionClassString);
        Set<String> ppStrings;
        try{
            ppStrings = Lexicon.getPOSForClass(prepositionClass,
                Lexicon.LexicalEntry.PART_OF_SPEECH.RELATIONAL_PREPOSITIONAL_PHRASE,
                yodaEnvironment.nlg.grammarPreferences, false);
        } catch (Lexicon.NoLexiconEntryException e) {
            ppStrings = new HashSet<>();
        }

        for (String ppString : ppStrings) {
            JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\"" + prepositionClass.getSimpleName() + "\"}");
            SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(), hasQualityRole);
            prepositionChunks.put(ppString, tmp);
        }

        Map<String, JSONObject> childChunks = yodaEnvironment.nlg.generateAll(SemanticsModel.parseJSON(child),
                yodaEnvironment, remainingDepth - 1);

        Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
        childNodeChunks.put(hasQualityRole+"."+InRelationTo.class.getSimpleName(), new ImmutablePair<>(1,1));
        return GenerationUtils.simpleOrderedCombinations(Arrays.asList(prepositionChunks, childChunks),
                PPTemplate0::compositionFunction, childNodeChunks, yodaEnvironment);
    }

    private static JSONObject compositionFunction(List<JSONObject> children){
        JSONObject pp = children.get(0);
        JSONObject child = children.get(1);

        // insert the child inside the preposition
        List<Object> ppKeys = new LinkedList<Object>(pp.keySet());
        ppKeys.remove("class");
        String hasPPQualityRole = (String) ppKeys.get(0);
        ((JSONObject)pp.get(hasPPQualityRole)).put(InRelationTo.class.getSimpleName(), child);

        SemanticsModel ans = new SemanticsModel(pp.toJSONString());
        ans.extendAndOverwrite(new SemanticsModel(pp.toJSONString()));
        return ans.getInternalRepresentation();
    }


}
