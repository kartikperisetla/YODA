package edu.cmu.sv.natural_language_generation.phrase_generators;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.adjective.Adjective;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.preposition.Preposition;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.PhraseGenerationRoutine;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/29/14.
 */
public class IndefiniteDescriptionGenerator implements PhraseGenerationRoutine {
    @Override
    public ImmutablePair<String, JSONObject> generate(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        Class<? extends Thing> nounClass = null;
        Map<Object, JSONObject> prepositionDescriptors = new HashMap<>();
        Map<Object, JSONObject> adjectiveDescriptors = new HashMap<>();
        if (constraints.containsKey("class")) {
            nounClass = Ontology.thingNameMap.get((String) constraints.get("class"));
        }
        for (Object key : constraints.keySet()) {
            if (key.equals("refType") || key.equals("class"))
                continue;
            if (!Ontology.roleNameMap.containsKey(key))
                continue;
            if (Ontology.adjectiveOrPrepositionInRange(Ontology.roleNameMap.get(key)).equals(Adjective.class))
                adjectiveDescriptors.put(key, (JSONObject) constraints.get(key));
            else if (Ontology.adjectiveOrPrepositionInRange(Ontology.roleNameMap.get(key)).equals(Preposition.class))
                prepositionDescriptors.put(key, (JSONObject) constraints.get(key));
        }

        String ans = "";
        SemanticsModel ansObject = new SemanticsModel("{}");

        // if no noun class, leave out the determiner, make the class Unk
        if (nounClass!=null && nounClass.equals(UnknownThingWithRoles.class))
            nounClass=null;
        if (nounClass!=null)
            ans = "a ";

        // add adjective chunks
        int adjectivesAddedCounter = 0;
        for (Object key : adjectiveDescriptors.keySet()) {
            JSONObject adjectiveContent = SemanticsModel.parseJSON(adjectiveDescriptors.get(key).toJSONString());
            SemanticsModel.wrap(adjectiveContent, UnknownThingWithRoles.class.getSimpleName(), (String) key);
            ImmutablePair<String, JSONObject> adjPhrase = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(adjectiveContent).
                    generate(adjectiveContent, yodaEnvironment);

            adjectivesAddedCounter += 1;
            if (adjectivesAddedCounter == adjectiveDescriptors.size() - 1 &&
                    nounClass == null &&
                    adjectiveDescriptors.size() > 1)
                ans += "and ";

            ans += adjPhrase.getLeft() + " ";
            ansObject.extendAndOverwrite(new SemanticsModel(adjPhrase.getRight().toJSONString()));
        }

        // add noun
        if (nounClass!=null){
            String singularNounForm = null;
            try {
                singularNounForm = yodaEnvironment.lex.getPOSForClass(nounClass,
                        Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, false).stream().findAny().get();
            } catch (Lexicon.NoLexiconEntryException e) {}
            ans += singularNounForm;
            ansObject.extendAndOverwrite(new SemanticsModel("{\"class\":\"" + nounClass.getSimpleName() + "\"}"));
            if (prepositionDescriptors.size()>0)
                ans+=" ";
        }

        // add PPs
        for (Object key : prepositionDescriptors.keySet()){
            JSONObject prepositionContent = SemanticsModel.parseJSON(prepositionDescriptors.get(key).toJSONString());
            SemanticsModel.wrap(prepositionContent, UnknownThingWithRoles.class.getSimpleName(), (String) key);
            ImmutablePair<String, JSONObject> prepPhrase = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(prepositionContent).
                    generate(prepositionContent, yodaEnvironment);

            if (nounClass == null &&
                    adjectiveDescriptors.size() > 1)
                ans += "and ";

            ans += prepPhrase.getLeft();
            ansObject.extendAndOverwrite(new SemanticsModel(prepPhrase.getRight().toJSONString()));
        }

        return new ImmutablePair<>(ans, ansObject.getInternalRepresentation());
    }
}
