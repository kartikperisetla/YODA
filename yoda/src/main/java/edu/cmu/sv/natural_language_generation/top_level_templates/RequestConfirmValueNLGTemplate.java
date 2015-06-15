package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.PhraseGenerationRoutine;
import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 10/29/14.
 */
public class RequestConfirmValueNLGTemplate implements TopLevelNLGTemplate {
    @Override
    public ImmutablePair<String, SemanticsModel> generate(SemanticsModel constraints, YodaEnvironment yodaEnvironment) {
        JSONObject topicWebResource = (JSONObject) constraints.newGetSlotPathFiller("topic");
        PhraseGenerationRoutine topicRoutine = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(topicWebResource);
        ImmutablePair<String, JSONObject> topicPhraseContent = topicRoutine.generate(topicWebResource, yodaEnvironment);

        String ansString = "you mean "+topicPhraseContent.getLeft()+" is that right?";
        SemanticsModel ansObject = new SemanticsModel("{\"dialogAct\":\""+RequestConfirmValue.class.getSimpleName()+
                "\", \"topic\": "+topicPhraseContent.getRight().toJSONString()+"}");
        return new ImmutablePair<>(ansString, ansObject);
    }

}
