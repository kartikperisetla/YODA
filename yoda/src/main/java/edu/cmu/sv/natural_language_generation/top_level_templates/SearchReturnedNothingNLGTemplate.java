package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.SearchReturnedNothing;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;


/**
 * Created by David Cohen on 11/13/14.
 */
public class SearchReturnedNothingNLGTemplate implements TopLevelNLGTemplate {
    @Override
    public ImmutablePair<String, SemanticsModel> generate(SemanticsModel constraints, YodaEnvironment yodaEnvironment) {
        JSONObject patientConstraint;
        JSONObject verbConstraint = (JSONObject) constraints.newGetSlotPathFiller("verb");
        patientConstraint = (JSONObject) verbConstraint.get(YodaSkeletonOntologyRegistry.patient.name);

        String ansString = "i don't know of ";

        ImmutablePair<String, JSONObject> patientPhrase = NaturalLanguageGenerator.getAppropriatePhraseGenerationRoutine(patientConstraint).
                generate(patientConstraint, yodaEnvironment);
        System.err.println("SearchReturnedNothingNLG:paitentPhrase:\n"+patientPhrase);
        System.err.println("patientconstraint"+patientConstraint);

        JSONObject patient = patientPhrase.getRight();
        ansString += patientPhrase.getLeft();

        String empty = "{\"class\":\""+YodaSkeletonOntologyRegistry.unknownThingWithRoles.name+"\"}";
        SemanticsModel ansObject = new SemanticsModel("{\"dialogAct\":\""+SearchReturnedNothing.class.getSimpleName()+
                "\", \"verb\": {\"class\":\""+
                YodaSkeletonOntologyRegistry.hasProperty.name+"\", \""+
                YodaSkeletonOntologyRegistry.patient.name+"\":"+empty+"}}");

        ansObject.extendAndOverwriteAtPoint("verb." + YodaSkeletonOntologyRegistry.patient.name, new SemanticsModel(patient.toJSONString()));
        return new ImmutablePair<>(ansString, ansObject);
    }
}
