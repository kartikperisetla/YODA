package edu.cmu.sv.natural_language_generation.internal_templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 */
public class NounClassTopLevelNLGTemplate implements TopLevelNLGTemplate {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        Class<? extends Thing> nounClass;
        // ensure that the constraints match this template
        try {
            Assert.verify(constraints.containsKey("class"));
            Assert.verify(constraints.keySet().size()==1);
            nounClass = Ontology.thingNameMap.get((String) constraints.get("class"));
            Assert.verify(Noun.class.isAssignableFrom(nounClass));
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }

        Map<String, JSONObject> clsChunks = new HashMap<>();
        Set<String> singularNounForms;
        try {
            singularNounForms = yodaEnvironment.lex.getPOSForClassHierarchy(nounClass,
                    Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, yodaEnvironment.nlg.grammarPreferences, false);
        } catch (Lexicon.NoLexiconEntryException e) {
            singularNounForms = new HashSet<>();
        }
        for (String singularNounForm : singularNounForms) {
            clsChunks.put("the "+ singularNounForm, SemanticsModel.parseJSON(constraints.toJSONString()));
        }
        Map<String, JSONObject> ans = new HashMap<>();
        GenerationUtils.simpleOrderedCombinations(Arrays.asList(clsChunks),
                x -> x.get(0), new HashMap<>(), yodaEnvironment).entrySet().forEach(x -> ans.put(x.getKey(), x.getValue()));

        return ans;
    }
}
