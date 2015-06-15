package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.PhraseGenerationRoutine;
import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 11/1/14.
 *
 * A special NLG template for requesting roles when it is the agent role that's requested
 *
 */
public class RequestAgentNLGTemplate implements TopLevelNLGTemplate {
    @Override
    public ImmutablePair<String, SemanticsModel> generate(SemanticsModel constraints, YodaEnvironment yodaEnvironment) {
        JSONObject verbObject = (JSONObject) constraints.newGetSlotPathFiller("verb");
        JSONObject patientDescription = (JSONObject) verbObject.get("Patient");

        PhraseGenerationRoutine patientRoutine = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(patientDescription);
        ImmutablePair<String, JSONObject> patientPhraseContent = patientRoutine.generate(patientDescription, yodaEnvironment);

        SemanticsModel ansModel = constraints.deepCopy();
        SemanticsModel.putAtPath(ansModel.getInternalRepresentation(), "verb.Patient", patientPhraseContent.getRight());

        return new ImmutablePair<>("is what " + patientPhraseContent.getLeft() + "?", ansModel);
    }
}
