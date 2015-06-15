package edu.cmu.sv.natural_language_generation.phrase_generators;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.PhraseGenerationRoutine;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 10/29/14.
 */
public class NounClassGenerator implements PhraseGenerationRoutine {
    @Override
    public ImmutablePair<String, JSONObject> generate(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        Class<? extends Thing> nounClass;
        nounClass = Ontology.thingNameMap.get((String) constraints.get("class"));

        String singularNounForm = null;
        try {
            singularNounForm = yodaEnvironment.lex.getPOSForClassHierarchy(nounClass,
                    Lexicon.LexicalEntry.PART_OF_SPEECH.SINGULAR_NOUN, false).stream().findAny().get();
        } catch (Lexicon.NoLexiconEntryException e) {}

        return new ImmutablePair<>("the "+singularNounForm, SemanticsModel.parseJSON(constraints.toJSONString()));
    }
}
