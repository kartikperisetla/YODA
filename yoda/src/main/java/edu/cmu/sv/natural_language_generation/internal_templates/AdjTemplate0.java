package edu.cmu.sv.natural_language_generation.internal_templates;

import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 11/13/14.
 */
public class AdjTemplate0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        String hasQualityRole;
        String adjectiveClassString;
        // ensure that the constraints match this template
        try {
            Assert.verify(constraints.get("class").equals(UnknownThingWithRoles.class.getSimpleName()));
            Assert.verify(constraints.keySet().size()==2);
            List<String> keys = (List<String>) constraints.keySet().stream().map(x -> (String)x).collect(Collectors.toList());
            keys.remove("class");
            hasQualityRole = keys.get(0);
            JSONObject adjectiveContent = (JSONObject) constraints.get(hasQualityRole);
            Assert.verify(adjectiveContent.keySet().size()==1);
            Assert.verify(adjectiveContent.containsKey("class"));
            adjectiveClassString = (String) adjectiveContent.get("class");
            Assert.verify(OntologyRegistry.thingNameMap.containsKey(adjectiveClassString));
            Assert.verify(Adjective.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(adjectiveClassString)));
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }

        Map<String, JSONObject> adjectiveChunks = new HashMap<>();
        Class<? extends Thing> adjectiveClass = OntologyRegistry.thingNameMap.get(adjectiveClassString);
        Set<String> adjectiveStrings = Lexicon.getPOSForClass(adjectiveClass, "adjectives", yodaEnvironment);

        for (String ppString : adjectiveStrings) {
            JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\"" + adjectiveClass.getSimpleName() + "\"}");
            SemanticsModel.wrap(tmp, UnknownThingWithRoles.class.getSimpleName(), hasQualityRole);
            adjectiveChunks.put(ppString, tmp);
        }

        return GenerationUtils.simpleOrderedCombinations(Arrays.asList(adjectiveChunks),
                AdjTemplate0::compositionFunction, new HashMap<>(), yodaEnvironment);
    }

    private static JSONObject compositionFunction(List<JSONObject> children){
        return children.get(0);
    }


}
