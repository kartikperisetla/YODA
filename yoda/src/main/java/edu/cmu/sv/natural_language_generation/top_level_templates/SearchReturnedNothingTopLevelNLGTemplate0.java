package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.Patient;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.SearchReturnedNothing;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by David Cohen on 11/13/14.
 */
public class SearchReturnedNothingTopLevelNLGTemplate0 implements TopLevelNLGTemplate {

    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        JSONObject patientConstraint;

        // ensure that the constraints match this template
        try {
            Assert.verify(constraints.keySet().size()==2);
            Assert.verify(constraints.containsKey("dialogAct") && constraints.containsKey("verb"));
            Assert.verify(constraints.get("dialogAct").equals(SearchReturnedNothing.class.getSimpleName()));
            JSONObject verbConstraint = (JSONObject) constraints.get("verb");
            Assert.verify(verbConstraint.keySet().size()==2);
            Assert.verify(verbConstraint.get("class").equals(HasProperty.class.getSimpleName()));
            Assert.verify(verbConstraint.containsKey(Patient.class.getSimpleName()));
            patientConstraint = (JSONObject) verbConstraint.get(Patient.class.getSimpleName());
//            Assert.verify(patientConstraint.get("class").equals(UnknownThingWithRoles.class.getSimpleName()));
        } catch (Assert.AssertException e) {
            return new HashMap<>();
        }
//        System.err.println("SearchReturnedNothingTemplate0: here");
//        System.err.println(constraints);
//        System.err.println("patient constraint:" + patientConstraint);

        Map<String, JSONObject> toBeChunks = new HashMap<>();
        toBeChunks.put("i don't know of", new JSONObject());

        Map<String, JSONObject> patientChunks = yodaEnvironment.nlg.
                generateAll(patientConstraint, yodaEnvironment, yodaEnvironment.nlg.grammarPreferences.maxNounPhraseDepth);
//        System.err.println("patient chunks:" + patientChunks);

        Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
        childNodeChunks.put("verb."+Patient.class.getSimpleName(), new ImmutablePair<>(1,1));
        return GenerationUtils.simpleOrderedCombinations(Arrays.asList(toBeChunks, patientChunks),
                SearchReturnedNothingTopLevelNLGTemplate0::compositionFunction, childNodeChunks, yodaEnvironment);
    }

    private static JSONObject compositionFunction(List<JSONObject> children){
        String empty = "{\"class\":\""+UnknownThingWithRoles.class.getSimpleName()+"\"}";
        JSONObject dontKnowStuff = children.get(0);
        JSONObject patient = children.get(1);

        SemanticsModel ans = new SemanticsModel("{\"dialogAct\":\""+SearchReturnedNothing.class.getSimpleName()+
                "\", \"verb\": {\"class\":\""+
                HasProperty.class.getSimpleName()+"\", \""+
                Patient.class.getSimpleName()+"\":"+empty+"}}");

        ans.extendAndOverwriteAtPoint("verb."+Patient.class.getSimpleName(), new SemanticsModel(patient.toJSONString()));
        return ans.getInternalRepresentation();
    }
}
