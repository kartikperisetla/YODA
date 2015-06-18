package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.PhraseGenerationRoutine;
import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Statement;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;


/**
 * Created by David Cohen on 11/13/14.
 */
public class StatementTopLevelNLGTemplate implements TopLevelNLGTemplate {
    @Override
    public ImmutablePair<String, SemanticsModel> generate(SemanticsModel constraints, YodaEnvironment yodaEnvironment) {
        JSONObject agentConstraint;
        JSONObject patientConstraint;

        JSONObject verbConstraint = (JSONObject) constraints.newGetSlotPathFiller("verb");
        agentConstraint = (JSONObject) verbConstraint.get(YodaSkeletonOntologyRegistry.agent.name);
        patientConstraint = (JSONObject) verbConstraint.get(YodaSkeletonOntologyRegistry.patient.name);

        if (!verbConstraint.get("class").equals(YodaSkeletonOntologyRegistry.hasProperty.name))
            throw new Error("can only generate statements for "+YodaSkeletonOntologyRegistry.hasProperty.name+":\n"+constraints);

        PhraseGenerationRoutine agentRoutine = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(agentConstraint);
        ImmutablePair<String, JSONObject> agentPhraseContent = agentRoutine.generate(agentConstraint, yodaEnvironment);
        PhraseGenerationRoutine patientRoutine = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(patientConstraint);
        ImmutablePair<String, JSONObject> patientPhraseContent = patientRoutine.generate(patientConstraint, yodaEnvironment);

        String ansString = agentPhraseContent.getLeft() + " is " + patientPhraseContent.getLeft();

        String empty = "{\"class\":\""+YodaSkeletonOntologyRegistry.unknownThingWithRoles.name+"\"}";
        SemanticsModel ansModel = new SemanticsModel("{\"dialogAct\":\""+Statement.class.getSimpleName()+
                "\", \"verb\": {\"class\":\""+
                YodaSkeletonOntologyRegistry.hasProperty.name+"\", \""+
                YodaSkeletonOntologyRegistry.agent.name+"\":"+empty+", \""+
                YodaSkeletonOntologyRegistry.patient.name+"\":"+empty+"}}");

        ansModel.extendAndOverwriteAtPoint("verb." + YodaSkeletonOntologyRegistry.agent.name,
                new SemanticsModel(agentPhraseContent.getRight().toJSONString()));
        ansModel.extendAndOverwriteAtPoint("verb." + YodaSkeletonOntologyRegistry.patient.name,
                new SemanticsModel(patientPhraseContent.getRight().toJSONString()));
        return new ImmutablePair<>(ansString, ansModel);
    }
}
