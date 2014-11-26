package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

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
        SemanticsModel spokenByThem = hypothesis.getSpokenByThem();
        String verb = (String)spokenByThem.newGetSlotPathFiller("verb.class");
        Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);
        if (verbClass.equals(HasProperty.class)){
            slotPathsToResolve.add("verb.Agent");
            // sort node paths so that nested children aren't checked before their parents
            for (String path : hypothesis.getSpokenByThem().getAllInternalNodePaths().stream().
                    sorted((x,y) -> Integer.compare(x.length(), y.length())).collect(Collectors.toList())){
                if (slotPathsToResolve.contains(path)
                        || Arrays.asList("", "dialogAct", "verb").contains(path)
                        || slotPathsToResolve.stream().anyMatch(x -> path.startsWith(x)))
                    continue;
                if (((JSONObject)spokenByThem.newGetSlotPathFiller(path)).get("class").equals(UnknownThingWithRoles.class.getSimpleName()))
                    continue;
                slotPathsToResolve.add(path);
            }
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
        Map<String, Double> ynqTruth = new HashMap<>();
        for (String groundedHypothesisID : groundedDiscourseUnitHypothesis.getGroundedHypotheses().keySet()) {
            SemanticsModel hypothesis = groundedDiscourseUnitHypothesis.getGroundedHypotheses().get(groundedHypothesisID);
            String verb = (String) hypothesis.newGetSlotPathFiller("verb.class");
            Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);
            if (verbClass.equals(HasProperty.class)) {
                ynqTruth.put(groundedHypothesisID, ReferenceResolution.descriptionMatch(yodaEnvironment,
                        (JSONObject) hypothesis.newGetSlotPathFiller("verb.Agent"),
                        (JSONObject) hypothesis.newGetSlotPathFiller("verb.Patient")));
            }
        }
        groundedDiscourseUnitHypothesis.setYnqTruth(ynqTruth);
    }
}
