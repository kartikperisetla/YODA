package edu.cmu.sv.natural_language_generation.phrase_generators;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.InRelationTo;
import edu.cmu.sv.natural_language_generation.Lexicon;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.PhraseGenerationRoutine;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 11/13/14.
 */
public class PrepositionGenerator implements PhraseGenerationRoutine{
    @Override
    public ImmutablePair<String, JSONObject> generate(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        String hasQualityRole;
        String prepositionClassString;
        JSONObject child;
        List<String> keys = (List<String>) constraints.keySet().stream().map(x -> (String) x).collect(Collectors.toList());
        keys.remove("class");
        hasQualityRole = keys.get(0);
        JSONObject prepositionContent = (JSONObject) constraints.get(hasQualityRole);
        prepositionClassString = (String) prepositionContent.get("class");
        child = SemanticsModel.parseJSON(((JSONObject) prepositionContent.get(InRelationTo.class.getSimpleName())).toJSONString());

        Class<? extends Thing> prepositionClass = Ontology.thingNameMap.get(prepositionClassString);
        String ppString = null;
        try {
            ppString = yodaEnvironment.lex.getPOSForClass(prepositionClass,
                    Lexicon.LexicalEntry.PART_OF_SPEECH.PREPOSITION, false).stream().findAny().get();
        } catch (Lexicon.NoLexiconEntryException e) {}

        JSONObject ansJSON = SemanticsModel.parseJSON("{\"class\":\"" + prepositionClass.getSimpleName() + "\"}");
        SemanticsModel.wrap(ansJSON, UnknownThingWithRoles.class.getSimpleName(), hasQualityRole);

        ImmutablePair<String, JSONObject> nestedPhrase = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(child).
                generate(child, yodaEnvironment);

        SemanticsModel.putAtPath(ansJSON, hasQualityRole+"."+InRelationTo.class.getSimpleName(), nestedPhrase.getRight());
        String ansString = ppString + " " + nestedPhrase.getLeft();

        return new ImmutablePair<>(ansString, ansJSON);
    }


}
