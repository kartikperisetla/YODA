package edu.cmu.sv.natural_language_generation.internal_templates;

import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.Ontology;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.adjective.Adjective;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.preposition.Preposition;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 */
public class IndefiniteDescriptionTemplate0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        Class<? extends Thing> nounClass = null;
        Map<Object, JSONObject> prepositionDescriptors = new HashMap<>();
        Map<Object, JSONObject> adjectiveDescriptors = new HashMap<>();
        try {
            Assert.verify(constraints.containsKey("refType"));
            Assert.verify(constraints.get("refType").equals("indefinite"));
            if (constraints.containsKey("class")) {
                nounClass = Ontology.thingNameMap.get((String) constraints.get("class"));
                Assert.verify(Noun.class.isAssignableFrom(nounClass));
            }
            for (Object key : constraints.keySet()){
                if (key.equals("refType") || key.equals("class"))
                    continue;
                if (!Ontology.roleNameMap.containsKey(key))
                    continue;
                if (Ontology.adjectiveOrPrepositionInRange(Ontology.roleNameMap.get(key)).equals(Adjective.class))
                    adjectiveDescriptors.put(key, (JSONObject) constraints.get(key));
                else if (Ontology.adjectiveOrPrepositionInRange(Ontology.roleNameMap.get(key)).equals(Preposition.class))
                    prepositionDescriptors.put(key, (JSONObject) constraints.get(key));
            }
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }

        List<Map<String, JSONObject>> chunks = new LinkedList<>();
        // if no noun class, leave out the determiner, make the class Unk
        if (nounClass!=null){
            Map<String, JSONObject> detChunk = new HashMap<>();
            detChunk.put("a", SemanticsModel.parseJSON("{}"));
            chunks.add(detChunk);
        }
        // add adjective chunks
        int adjectivesAddedCounter = 0;
        for (Object key : adjectiveDescriptors.keySet()){
            Map<String, JSONObject> adjectiveChunk = new HashMap<>();
            JSONObject adjectiveContent = SemanticsModel.parseJSON(adjectiveDescriptors.get(key).toJSONString());
            SemanticsModel.wrap(adjectiveContent, UnknownThingWithRoles.class.getSimpleName(), (String) key);
            for (Map.Entry<String, JSONObject> entry : yodaEnvironment.nlg.
                    generateAll(adjectiveContent, yodaEnvironment, remainingDepth-1).entrySet()){
                adjectiveChunk.put(entry.getKey(), entry.getValue());
            }
            chunks.add(adjectiveChunk);

            adjectivesAddedCounter += 1;
            if (adjectivesAddedCounter==adjectiveDescriptors.size()-1 &&
                    nounClass==null &&
                    adjectiveDescriptors.size()>1){
                Map<String, JSONObject> andChunk = new HashMap<>();
                andChunk.put("and", SemanticsModel.parseJSON("{}"));
                chunks.add(andChunk);
            }
        }
        // add noun
        if (nounClass!=null){
            Map<String, JSONObject> clsChunk = new HashMap<>();
            Set<String> singularNounForms;
            try {
                singularNounForms = yodaEnvironment.lex.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, yodaEnvironment.nlg.grammarPreferences, false);
            } catch (Lexicon.NoLexiconEntryException e) {
                singularNounForms = new HashSet<>();
            }
            for (String singularNounForm : singularNounForms) {
                clsChunk.put(singularNounForm, SemanticsModel.parseJSON("{\"class\":\"" + nounClass.getSimpleName() + "\"}"));
            }
            chunks.add(clsChunk);
        }
        // add PPs
        for (Object key : prepositionDescriptors.keySet()){
            Map<String, JSONObject> prepositionChunk = new HashMap<>();
            JSONObject prepositionContent = SemanticsModel.parseJSON(prepositionDescriptors.get(key).toJSONString());
            SemanticsModel.wrap(prepositionContent, UnknownThingWithRoles.class.getSimpleName(), (String) key);
//            System.out.println("IndefiniteDescriptionTemplate: preposition content:" + prepositionContent);
            for (Map.Entry<String, JSONObject> entry : yodaEnvironment.nlg.
                    generateAll(prepositionContent, yodaEnvironment, remainingDepth).entrySet()){
                prepositionChunk.put(entry.getKey(), entry.getValue());
            }
            chunks.add(prepositionChunk);

            adjectivesAddedCounter += 1;
            if (adjectivesAddedCounter==adjectiveDescriptors.size()-1 &&
                    nounClass==null &&
                    adjectiveDescriptors.size()>1){
                Map<String, JSONObject> andChunk = new HashMap<>();
                andChunk.put("and", SemanticsModel.parseJSON("{}"));
                chunks.add(andChunk);
            }
        }


        Map<String, JSONObject> ans = new HashMap<>();
        GenerationUtils.simpleOrderedCombinations(chunks, IndefiniteDescriptionTemplate0::compositionFunction,
                new HashMap<>(), yodaEnvironment).entrySet().forEach(x -> ans.put(x.getKey(), x.getValue()));
        return ans;
    }

    private static JSONObject compositionFunction(List<JSONObject> children){
        SemanticsModel ans = new SemanticsModel(children.get(0).toJSONString());
        for (int i = 1; i < children.size(); i++) {
            ans.extendAndOverwrite(new SemanticsModel(children.get(i).toJSONString()));
        }
        return ans.getInternalRepresentation();
    }


}
