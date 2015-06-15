package edu.cmu.sv.natural_language_generation.nlg2_top_level_templates;

import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Patient;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.natural_language_generation.NLG2;
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
        patientConstraint = (JSONObject) verbConstraint.get(Patient.class.getSimpleName());

        String ansString = "i don't know of ";

        ImmutablePair<String, JSONObject> patientPhrase = NLG2.getAppropriatePhraseGenerationRoutine(patientConstraint).
                generate(patientConstraint, yodaEnvironment);

        JSONObject patient = patientPhrase.getRight();
        ansString += patientPhrase.getLeft();

        String empty = "{\"class\":\""+UnknownThingWithRoles.class.getSimpleName()+"\"}";
        SemanticsModel ansObject = new SemanticsModel("{\"dialogAct\":\""+SearchReturnedNothing.class.getSimpleName()+
                "\", \"verb\": {\"class\":\""+
                HasProperty.class.getSimpleName()+"\", \""+
                Patient.class.getSimpleName()+"\":"+empty+"}}");

        ansObject.extendAndOverwriteAtPoint("verb." + Patient.class.getSimpleName(), new SemanticsModel(patient.toJSONString()));
        return new ImmutablePair<>(ansString, ansObject);
    }
}
