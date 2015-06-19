package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.ontology2.Noun2;
import edu.cmu.sv.domain.ontology2.Role2;
import edu.cmu.sv.domain.ontology2.Verb2;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 10/17/14.
 */
public class Utils {

    public static double discourseUnitContextProbability(DialogState dialogState,
                                                         DiscourseUnit predecessor){
        return Math.pow(.1, numberOfIntermediateDiscourseUnitsBySpeaker(predecessor, dialogState, "system")) *
                Math.pow(.1, numberOfIntermediateDiscourseUnitsBySpeaker(predecessor, dialogState, "user")) *
                Math.pow(.1, numberOfLinksRespondingToDiscourseUnit(predecessor, dialogState));
    }

    public static int numberOfIntermediateDiscourseUnitsBySpeaker(DiscourseUnit predecessorDu,
                                                           DialogState dialogState, String speaker){
        int ans = 0;
        for (String discourseUnitIdentifier : dialogState.getDiscourseUnitHypothesisMap().keySet()){
            DiscourseUnit otherPredecessor = dialogState.getDiscourseUnitHypothesisMap().get(discourseUnitIdentifier);
            if (otherPredecessor==predecessorDu)
                continue;
            if (otherPredecessor.getInitiator().equals(speaker) &&
                    otherPredecessor.getMostRecentContributionTime() > predecessorDu.getMostRecentContributionTime())
                ans += 1;
        }
        return ans;
    }

    public static int numberOfLinksRespondingToDiscourseUnit(DiscourseUnit contextDu,
                                                             DialogState dialogState){
        int ans = 0;
        for (DialogState.ArgumentationLink link : dialogState.getArgumentationLinks()){
            if (dialogState.getDiscourseUnitHypothesisMap().get(link.getPredecessor())==contextDu)
                ans += 1;
        }
        return ans;
    }

    /*
    * return Triple<slotPathsToResolve, pathsToInfer, alreadyResolvedPaths>
    * */
    public static Triple<Set<String>, Set<String>, Set<String>> resolutionInformation(DiscourseUnit discourseUnit) {

        Set<String> slotPathsToResolve = new HashSet<>();
        SemanticsModel spokenByThem = discourseUnit.getSpokenByThem();
        SemanticsModel currentGroundedInterpretation = discourseUnit.getGroundInterpretation();
        String verb = (String) spokenByThem.newGetSlotPathFiller("verb.class");
        Verb2 verbClass = Ontology.verbNameMap.get(verb);
        Set<Role2> requiredGroundedRoles = verbClass.getRequiredGroundedRoles();
        Set<Role2> requiredDescriptions = verbClass.getRequiredDescriptions();

        for (String path : spokenByThem.getAllInternalNodePaths().stream().
                sorted((x, y) -> Integer.compare(x.length(), y.length())).collect(Collectors.toList())) {
            if (slotPathsToResolve.contains(path)
                    || Arrays.asList("", "dialogAct", "verb").contains(path)
                    || slotPathsToResolve.stream().anyMatch(x -> path.startsWith(x)))
                continue;
            if (!Ontology.nounNameMap.containsKey(((JSONObject) spokenByThem.newGetSlotPathFiller(path)).get("class")))
                continue;
            if (requiredDescriptions.stream().map(x -> "verb." + x.name).collect(Collectors.toList()).contains(path))
                continue;
            slotPathsToResolve.add(path);
        }


        // collect the paths that have already been resolved
        Set<String> alreadyResolvedPaths;
        if (currentGroundedInterpretation != null) {
            alreadyResolvedPaths = slotPathsToResolve.stream().filter(x -> currentGroundedInterpretation.newGetSlotPathFiller(x) != null).collect(Collectors.toSet());
        } else {
            alreadyResolvedPaths = new HashSet<>();
        }
        slotPathsToResolve.removeAll(alreadyResolvedPaths);

        //todo: add roles missing from prepositions
        Set<String> pathsToInfer = requiredGroundedRoles.stream().
                map(x -> "verb." + x.name).
                filter(x -> !alreadyResolvedPaths.contains(x)).
                filter(x -> !slotPathsToResolve.contains(x)).
                collect(Collectors.toSet());

        return new ImmutableTriple<>(slotPathsToResolve, pathsToInfer, alreadyResolvedPaths);
    }


    public static Set<String> filterSlotPathsByRangeClass(Set<String> slotPaths, String rangeClassName){
        Set<String> ans = new HashSet<>();
        for (String slotPath : slotPaths){
            String lastRoleName = slotPath.split("\\.")[slotPath.split("\\.").length-1];
            if (!Ontology.roleNameMap.containsKey(lastRoleName))
                continue;
            if (!Ontology.nounNameMap.containsKey(rangeClassName))
                continue;
            Role2 roleClass = Ontology.roleNameMap.get(lastRoleName);
            Noun2 contentClass = Ontology.nounNameMap.get(rangeClassName);
            Set<Object> range = roleClass.getRange();
            for (Object rangeCls : range) {
                if (rangeCls instanceof Noun2 && (Ontology.nounInherits((Noun2) rangeCls, contentClass) ||
                        Ontology.nounInherits(contentClass, (Noun2) rangeCls))) {
                    ans.add(slotPath);
                    break;
                }
            }
        }
        return ans;
    }

    /*
    * Update the discourse unit by bringing it back to a grounded state from a non-grounded state
    * Leave the resolved meanings from the initiator alone
    * */
    public static void returnToGround(DiscourseUnit predecessor,
                                      SemanticsModel newSpokenByInitiator,
                                      long timeStamp){
        if (predecessor.initiator.equals("user")){
            predecessor.timeOfLastActByThem = timeStamp;
            predecessor.spokenByThem = newSpokenByInitiator;
            predecessor.spokenByMe = null;
            predecessor.groundTruth = null;
            predecessor.timeOfLastActByMe = null;
        } else { // if predecessor.initiator.equals("system")
            predecessor.timeOfLastActByThem = null;
            predecessor.spokenByThem = null;
            predecessor.groundInterpretation = null;
            predecessor.spokenByMe = newSpokenByInitiator;
            predecessor.timeOfLastActByMe = timeStamp;
        }
    }

    /*
    * Update the discourse unit by un-grounding it
    * */
    public static void unground(DiscourseUnit predecessor,
                                      SemanticsModel newSpokenByOther,
                                      SemanticsModel groundedByOther,
                                      long timeStamp){
        if (predecessor.initiator.equals("user")){
            predecessor.spokenByMe = newSpokenByOther;
            predecessor.groundTruth = groundedByOther;
            predecessor.timeOfLastActByMe = timeStamp;
        } else { // if predecessor.initiator.equals("system")
            predecessor.spokenByThem = newSpokenByOther;
            predecessor.groundInterpretation = groundedByOther;
            predecessor.timeOfLastActByThem = timeStamp;
        }
    }


}
