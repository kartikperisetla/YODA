package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.verb.HasProperty;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.HypothesisSetManagement;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
    public Pair<Map<String, DiscourseUnitHypothesis>, StringDistribution> ground(DiscourseUnitHypothesis hypothesis, YodaEnvironment yodaEnvironment) {
        // get grounded hypotheses / corresponding weights
        Pair<Map<String, DiscourseUnitHypothesis>, StringDistribution> groundingHypotheses = groundHelper(hypothesis, yodaEnvironment);
        Map<String, DiscourseUnitHypothesis> discourseUnits = groundingHypotheses.getLeft();
        StringDistribution discourseUnitDistribution = groundingHypotheses.getRight();

//        // run analysis on each of the grounded hypotheses
//        for (String key : discourseUnits.keySet()){
//            analyse(discourseUnits.get(key), yodaEnvironment);
//        }

        discourseUnitDistribution.normalize();
        return new ImmutablePair<>(discourseUnits, discourseUnitDistribution);
    }

    private Pair<Map<String, DiscourseUnitHypothesis>, StringDistribution> groundHelper(DiscourseUnitHypothesis hypothesis, YodaEnvironment yodaEnvironment) {
        List<String> slotPathsToResolve = new LinkedList<>();
        SemanticsModel spokenByThem = hypothesis.getSpokenByThem();
        SemanticsModel currentGroundedInterpretation = hypothesis.getGroundInterpretation();
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
                if (!Noun.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(((JSONObject)spokenByThem.newGetSlotPathFiller(path)).get("class"))))
                    continue;
                slotPathsToResolve.add(path);
            }
//            System.out.println("RespondToYNQuestion.groundHelper: slotPathsToResolve:"+slotPathsToResolve);
        }

        // keep only the slot paths that haven't already been resolved
        Set<String> alreadyGroundedPaths;
        if (currentGroundedInterpretation!=null) {
            alreadyGroundedPaths = slotPathsToResolve.stream().filter(x -> currentGroundedInterpretation.newGetSlotPathFiller(x) != null).collect(Collectors.toSet());
        } else {
            alreadyGroundedPaths = new HashSet<>();
        }
        slotPathsToResolve.removeAll(alreadyGroundedPaths);
//        System.out.println("RespondToYNQuestion: already resolved:"+ alreadyGroundedPaths + ", slot paths to resolve:"+slotPathsToResolve);

        Map<String, StringDistribution> referenceMarginals = new HashMap<>();
        for (String slotPathToResolve : slotPathsToResolve) {
            referenceMarginals.put(slotPathToResolve,
                    ReferenceResolution.resolveReference(yodaEnvironment,
                            (JSONObject) hypothesis.getSpokenByThem().newGetSlotPathFiller(slotPathToResolve)));
        }
        Pair<StringDistribution, Map<String, Map<String, String>>> referenceJoint =
                HypothesisSetManagement.getJointFromMarginals(referenceMarginals, 10);
        Map<String, DiscourseUnitHypothesis> discourseUnits = new HashMap<>();

        for (String jointHypothesisID : referenceJoint.getKey().keySet()){
            DiscourseUnitHypothesis groundedDiscourseUnitHypothesis = hypothesis.deepCopy();
            SemanticsModel groundedModel = hypothesis.getSpokenByThem().deepCopy();
            Map<String, String> assignment = referenceJoint.getValue().get(jointHypothesisID);
            // add new bindings
            for (String slotPathVariable : assignment.keySet()){
                SemanticsModel.overwrite((JSONObject) groundedModel.newGetSlotPathFiller(slotPathVariable),
                        SemanticsModel.parseJSON(OntologyRegistry.WebResourceWrap(assignment.get(slotPathVariable))));
            }
            // include previously grounded paths
            for (String path : alreadyGroundedPaths){
                SemanticsModel.overwrite((JSONObject) groundedModel.newGetSlotPathFiller(path),
                        (JSONObject) currentGroundedInterpretation.newGetSlotPathFiller(path));
            }
            groundedDiscourseUnitHypothesis.setGroundInterpretation(groundedModel);
            discourseUnits.put(jointHypothesisID, groundedDiscourseUnitHypothesis);
        }

        return new ImmutablePair<>(discourseUnits, referenceJoint.getLeft());
    }

    public void analyse(DiscourseUnitHypothesis groundedDiscourseUnitHypothesis,
                        YodaEnvironment yodaEnvironment) {
        SemanticsModel hypothesis = groundedDiscourseUnitHypothesis.getGroundInterpretation();
        String verb = (String) hypothesis.newGetSlotPathFiller("verb.class");
        Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);
        if (verbClass.equals(HasProperty.class)) {
            groundedDiscourseUnitHypothesis.setYnqTruth(ReferenceResolution.descriptionMatch(yodaEnvironment,
                    (JSONObject) hypothesis.newGetSlotPathFiller("verb.Agent"),
                    (JSONObject) hypothesis.newGetSlotPathFiller("verb.Patient")));
        }
    }
}
