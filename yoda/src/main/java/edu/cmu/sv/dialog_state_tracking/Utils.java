package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Set;

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
    * Rank the possible places where the suggestion content might attach to the existing semantics model
    * */
    public static StringDistribution findPossiblePointsOfAttachment(SemanticsModel discourseUnitSemantics,
                                                                    /*SemanticsModel groundedSemantics,*/
                                                                    JSONObject suggestionContent){
        StringDistribution ans = new StringDistribution();
        Set<Object> verbRoles = ((JSONObject) discourseUnitSemantics.newGetSlotPathFiller("verb")).keySet();
        String contentClass = (String) suggestionContent.get("class");
        if (contentClass.equals(UnknownThingWithRoles.class.getSimpleName())){
            for (Object key : verbRoles){
                if (key.equals("class"))
                    continue;
                JSONObject filler = (JSONObject) discourseUnitSemantics.newGetSlotPathFiller("verb."+key);
                if (filler.get("class").equals(UnknownThingWithRoles.class.getSimpleName()))
                    ans.put("verb."+key, 1.0);
            }
        } else if (Noun.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(contentClass))) {
            for (Object key : verbRoles) {
                if (key.equals("class"))
                    continue;
                JSONObject filler = (JSONObject) discourseUnitSemantics.newGetSlotPathFiller("verb." + key);
                if (filler.get("class").equals(contentClass))
                    ans.put("verb." + key, 1.0);
                else if (OntologyRegistry.thingNameMap.get(contentClass).isAssignableFrom(OntologyRegistry.thingNameMap.get(filler.get("class"))) ||
                        OntologyRegistry.thingNameMap.get(filler.get("class")).isAssignableFrom(OntologyRegistry.thingNameMap.get(contentClass)))
                    ans.put("verb." + key, 1.0);
                else if (Noun.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(filler.get("class"))))
                    ans.put("verb." + key, .5);
            }
        }
        return ans;
    }

    public static StringDistribution findPossiblePointsOfAttachment(DiscourseUnit predecessorDiscourseUnit,
                                                                    JSONObject suggestionContent){
        StringDistribution ans = new StringDistribution();
        Set<Object> verbRoles = ((JSONObject) predecessorDiscourseUnit.
                getGroundInterpretation().newGetSlotPathFiller("verb")).keySet();
        Class<? extends Thing> contentClass = OntologyRegistry.thingNameMap.get(suggestionContent.get("class"));
        for (Object key : verbRoles){
            if (OntologyRegistry.roleNameMap.containsKey(key)){
                Class<? extends Role> roleClass = OntologyRegistry.roleNameMap.get(key);
                Set<Class<? extends Thing>> range = new HashSet<>();
                try {
                    range = roleClass.newInstance().getRange();
                } catch ( InstantiationException e) {
                } catch ( IllegalAccessException e) {
                    e.printStackTrace();
                    System.exit(0);
                }
                for (Class<? extends Thing> rangeCls : range) {
                    if (rangeCls.isAssignableFrom(contentClass)) {
                        ans.put("verb." + (String) key, 1.0);
                        break;
                    }
                }
            }
        }
//        System.out.println("DST.Utils: possible points of attachment:"+ans);
        return ans;
    }


    /*
    * Update the discourse unit by bringing it back to a grounded state from a non-grounded state
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
