package edu.cmu.sv.database.dialog_task;

import com.google.common.collect.Iterables;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.role.Role;
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
        discourseUnitDistribution.normalize();
        return new ImmutablePair<>(discourseUnits, discourseUnitDistribution);
    }

    private Pair<Map<String, DiscourseUnitHypothesis>, StringDistribution> groundHelper(DiscourseUnitHypothesis targetDiscourseUnit, YodaEnvironment yodaEnvironment) {
        List<String> slotPathsToResolve = new LinkedList<>();
        SemanticsModel spokenByThem = targetDiscourseUnit.getSpokenByThem();
        SemanticsModel currentGroundedInterpretation = targetDiscourseUnit.getGroundInterpretation();
        String verb = (String)spokenByThem.newGetSlotPathFiller("verb.class");
        Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);

        try {
            for (String path : targetDiscourseUnit.getSpokenByThem().getAllInternalNodePaths().stream().
                    sorted((x,y) -> Integer.compare(x.length(), y.length())).collect(Collectors.toList())){
                if (slotPathsToResolve.contains(path)
                        || Arrays.asList("", "dialogAct", "verb").contains(path)
                        || slotPathsToResolve.stream().anyMatch(x -> path.startsWith(x)))
                    continue;
                if (!Noun.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(((JSONObject)spokenByThem.newGetSlotPathFiller(path)).get("class"))))
                    continue;
                slotPathsToResolve.add(path);
            }

            // keep only the slot paths that haven't already been resolved
            Set<String> alreadyGroundedPaths;
            if (currentGroundedInterpretation!=null) {
                alreadyGroundedPaths = slotPathsToResolve.stream().filter(x -> currentGroundedInterpretation.newGetSlotPathFiller(x) != null).collect(Collectors.toSet());
            } else {
                alreadyGroundedPaths = new HashSet<>();
            }
            slotPathsToResolve.removeAll(alreadyGroundedPaths);

            // only attempt to resolve slots that have associated semantic information
            // do not try to resolve slots for which the verb only requires descriptions
            slotPathsToResolve = slotPathsToResolve.stream().
                    filter(x -> targetDiscourseUnit.getSpokenByThem().newGetSlotPathFiller(x)!=null).
                    collect(Collectors.toList());

            Set<Class <? extends Role>> requiredDescriptions = verbClass.newInstance().getRequiredDescriptions();
            slotPathsToResolve.removeAll(
                    requiredDescriptions.stream().
                            map(x -> "verb." + x.getSimpleName()).
                            collect(Collectors.toSet()));


            Map<String, StringDistribution> referenceMarginals = new HashMap<>();
            for (String slotPathToResolve : slotPathsToResolve) {
                referenceMarginals.put(slotPathToResolve,
                        ReferenceResolution.resolveReference(yodaEnvironment,
                                (JSONObject) targetDiscourseUnit.getSpokenByThem().newGetSlotPathFiller(slotPathToResolve)));
            }
            Pair<StringDistribution, Map<String, Map<String, String>>> referenceJoint =
                    HypothesisSetManagement.getJointFromMarginals(referenceMarginals, 10);
            Map<String, DiscourseUnitHypothesis> discourseUnits = new HashMap<>();

            for (String jointHypothesisID : referenceJoint.getKey().keySet()){
                DiscourseUnitHypothesis groundedDiscourseUnitHypothesis = targetDiscourseUnit.deepCopy();
                SemanticsModel groundedModel = targetDiscourseUnit.getSpokenByThem().deepCopy();
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

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public void analyse(DiscourseUnitHypothesis groundedDiscourseUnitHypothesis,
                        YodaEnvironment yodaEnvironment) {
        SemanticsModel groundedMeaning = groundedDiscourseUnitHypothesis.getGroundInterpretation();
        String verb = (String) groundedMeaning.newGetSlotPathFiller("verb.class");
        Class<? extends Verb> verbClass = OntologyRegistry.verbNameMap.get(verb);

        try {
            Verb verbInstance = verbClass.newInstance();
            for (Class<? extends Role> requiredRole : Iterables.concat(verbInstance.getRequiredDescriptions(), verbInstance.getRequiredGroundedRoles())){
                if (groundedMeaning.newGetSlotPathFiller("verb."+requiredRole.getSimpleName())==null)
                    return;
            }

            if (verbClass.equals(HasProperty.class)) {
                groundedDiscourseUnitHypothesis.setYnqTruth(ReferenceResolution.descriptionMatch(yodaEnvironment,
                        (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Agent"),
                        (JSONObject) groundedMeaning.newGetSlotPathFiller("verb.Patient")));
            }


        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }


    }
}
