package edu.cmu.sv.natural_language_generation.phrase_generators;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.natural_language_generation.Lexicon;
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
public class AdjectiveGenerator implements PhraseGenerationRoutine {
    @Override
    public ImmutablePair<String, JSONObject> generate(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        // required information to generate
        String hasQualityRole;
        String adjectiveClassString;
        List<String> keys = (List<String>) constraints.keySet().stream().map(x -> (String)x).collect(Collectors.toList());
        keys.remove("class");
        hasQualityRole = keys.get(0);
        JSONObject adjectiveContent = (JSONObject) constraints.get(hasQualityRole);
        adjectiveClassString = (String) adjectiveContent.get("class");

        try {
            Class<? extends Thing> adjectiveClass = Ontology.thingNameMap.get(adjectiveClassString);
            String adjectiveString = null;
            adjectiveString = yodaEnvironment.lex.getPOSForClass(adjectiveClass, Lexicon.LexicalEntry.PART_OF_SPEECH.ADJECTIVE, false).stream().findAny().get();

            JSONObject tmp = SemanticsModel.parseJSON("{\"class\":\"" + adjectiveClass.getSimpleName() + "\"}");
            SemanticsModel.wrap(tmp, YodaSkeletonOntologyRegistry.unknownThingWithRoles.name, hasQualityRole);
            return new ImmutablePair<>(adjectiveString, tmp);

        } catch (Lexicon.NoLexiconEntryException e) {}
        return null;
    }
}
