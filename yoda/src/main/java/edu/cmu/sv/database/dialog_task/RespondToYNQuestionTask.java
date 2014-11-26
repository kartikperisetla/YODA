package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 9/3/14.
 *
 * This task answers a yes/no question by performing appropriate database lookups
 */
public class RespondToYNQuestionTask extends DialogTask {
    @Override
    public DiscourseUnit2.GroundedDiscourseUnitHypotheses ground(DiscourseUnit2.DiscourseUnitHypothesis hypothesis,
                                                                 YodaEnvironment yodaEnvironment) {
        List<String> slotPathsToResolve = new LinkedList<>();
        String verb = (String)hypothesis.getSpokenByThem().newGetSlotPathFiller("verb.class");
        Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);
        if (verbClass.equals(HasProperty.class)){
            slotPathsToResolve.add("verb.Agent");
        }

        Map<String, StringDistribution> referenceMarginals = new HashMap<>();
        for (String slotPathToResolve : slotPathsToResolve) {
            referenceMarginals.put(slotPathToResolve,
                    ReferenceResolution.resolveReference(yodaEnvironment,
                            (JSONObject) hypothesis.getSpokenByThem().newGetSlotPathFiller(slotPathToResolve)));
        }
        Pair<StringDistribution, Map<String, Map<String, String>>> referenceJoint =
                HypothesisSetManagement.getJointFromMarginals(referenceMarginals, 10);
        Map<String, SemanticsModel> groundedHypotheses = new HashMap<>();
        for (String jointHypothesisID : referenceJoint.getKey().keySet()){
            SemanticsModel groundedModel = hypothesis.getSpokenByThem().deepCopy();
            Map<String, String> assignment = referenceJoint.getValue().get(jointHypothesisID);
            for (String slotPathVariable : assignment.keySet()){
                SemanticsModel.overwrite((JSONObject) groundedModel.newGetSlotPathFiller(slotPathVariable),
                        SemanticsModel.parseJSON(OntologyRegistry.WebResourceWrap(assignment.get(slotPathVariable))));
            }
            groundedHypotheses.put(jointHypothesisID, groundedModel);
        }

        return new DiscourseUnit2.GroundedDiscourseUnitHypotheses(groundedHypotheses, referenceJoint.getKey());
    }

    @Override
    public void analyse(DiscourseUnit2.GroundedDiscourseUnitHypotheses groundedDiscourseUnitHypothesis,
                        YodaEnvironment yodaEnvironment) {
        for (String groundedHypothesisID : groundedDiscourseUnitHypothesis.getGroundedHypotheses().keySet()) {
            SemanticsModel hypothesis = groundedDiscourseUnitHypothesis.getGroundedHypotheses().get(groundedHypothesisID);
            String verb = (String) hypothesis.newGetSlotPathFiller("verb.class");
            Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);
            if (verbClass.equals(HasProperty.class)) {
                // todo: assemble a query, run it, and update the groundedDiscourseUnitHypothesis' analysis content


            }
        }
    }
}
