package edu.cmu.sv.natural_language_generation.phrase_generators;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology.QualityDegree;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
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
public class InverseRelationGenerator implements PhraseGenerationRoutine{
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
        child = SemanticsModel.parseJSON(((JSONObject) prepositionContent.get(YodaSkeletonOntologyRegistry.inRelationTo.name)).toJSONString());

        QualityDegree prepositionClass = Ontology.qualityDegreeNameMap.get(prepositionClassString);
        String ppString = "of";

        JSONObject ansJSON = SemanticsModel.parseJSON("{\"class\":\"" + prepositionClass.name + "\"}");
        SemanticsModel.wrap(ansJSON, YodaSkeletonOntologyRegistry.unknownThingWithRoles.name, hasQualityRole);

        ImmutablePair<String, JSONObject> nestedPhrase = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(child).
                generate(child, yodaEnvironment);

        SemanticsModel.putAtPath(ansJSON, hasQualityRole+"."+YodaSkeletonOntologyRegistry.inRelationTo.name, nestedPhrase.getRight());
        String ansString = ppString + " " + nestedPhrase.getLeft();

        return new ImmutablePair<>(ansString, ansJSON);
    }


}
