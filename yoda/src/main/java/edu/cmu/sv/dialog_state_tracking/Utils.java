package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by David Cohen on 10/17/14.
 */
public class Utils {

    public static double discourseUnitContextProbability(DialogStateHypothesis dialogStateHypothesis,
                                                         DiscourseUnitHypothesis predecessor){
        return Math.pow(.1, numberOfIntermediateDiscourseUnitsBySpeaker(predecessor, dialogStateHypothesis, "system")) *
                Math.pow(.1, numberOfIntermediateDiscourseUnitsBySpeaker(predecessor, dialogStateHypothesis, "user")) *
                Math.pow(.1, numberOfLinksRespondingToDiscourseUnit(predecessor, dialogStateHypothesis));
    }

    public static int numberOfIntermediateDiscourseUnitsBySpeaker(DiscourseUnitHypothesis predecessorDu,
                                                           DialogStateHypothesis dialogStateHypothesis, String speaker){
        int ans = 0;
        for (String discourseUnitIdentifier : dialogStateHypothesis.getDiscourseUnitHypothesisMap().keySet()){
            DiscourseUnitHypothesis otherPredecessor = dialogStateHypothesis.getDiscourseUnitHypothesisMap().get(discourseUnitIdentifier);
            if (otherPredecessor==predecessorDu)
                continue;
            if (otherPredecessor.getInitiator().equals(speaker) &&
                    otherPredecessor.getMostRecentContributionTime() > predecessorDu.getMostRecentContributionTime())
                ans += 1;
        }
        return ans;
    }

    public static int numberOfLinksRespondingToDiscourseUnit(DiscourseUnitHypothesis contextDu,
                                                             DialogStateHypothesis dialogStateHypothesis){
        int ans = 0;
        for (DialogStateHypothesis.ArgumentationLink link : dialogStateHypothesis.getArgumentationLinks()){
            if (dialogStateHypothesis.getDiscourseUnitHypothesisMap().get(link.getPredecessor())==contextDu)
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

    /*
    * Update the discourse unit by bringing it back to a grounded state from a non-grounded state
    * */
    public static void returnToGround(DiscourseUnitHypothesis predecessor,
                                      SemanticsModel newSpokenByInitiator,
                                      long timeStamp){
        if (predecessor.initiator.equals("user")){
            predecessor.timeOfLastActByThem = timeStamp;
            predecessor.spokenByThem = newSpokenByInitiator;
            predecessor.spokenByMe = null;
            predecessor.timeOfLastActByMe = null;
        } else { // if predecessor.initiator.equals("system")
            predecessor.timeOfLastActByThem = null;
            predecessor.spokenByThem = null;
            predecessor.spokenByMe = newSpokenByInitiator;
            predecessor.timeOfLastActByMe = timeStamp;
        }
    }

    /*
    * Update the discourse unit by un-grounding it
    * */
    public static void unground(DiscourseUnitHypothesis predecessor,
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



    /*
    * A class used to perform common types of analysis on a discourse unit and store results
    * analysis methods may throw AssertException if the type of analysis requested is not applicable
    * modifying the stored analysis result JSONObjects should modify the source discourse unit
    * */
    public static class DiscourseUnitAnalysis{
        private DiscourseUnitHypothesis discourseUnitHypothesis;
        private YodaEnvironment yodaEnvironment;
        public String suggestionPath;
        public JSONObject suggestedContent;
        public JSONObject groundedSuggestionIndividual;
        public Double descriptionMatch;

        public DiscourseUnitAnalysis(DiscourseUnitHypothesis discourseUnitHypothesis, YodaEnvironment yodaEnvironment) {
            this.discourseUnitHypothesis = discourseUnitHypothesis;
            this.yodaEnvironment = yodaEnvironment;
        }

        public boolean ungroundedByAct(Class<? extends DialogAct> ungroundingAction) throws Assert.AssertException {
            if (discourseUnitHypothesis.initiator.equals("user")){
                Assert.verify(discourseUnitHypothesis.spokenByMe!= null);
                Assert.verify(discourseUnitHypothesis.groundTruth != null);
                return discourseUnitHypothesis.groundTruth.newGetSlotPathFiller("dialogAct").
                        equals(ungroundingAction.getSimpleName());
            } else { //discourseUnitHypothesis.initiator.equals("system")
                Assert.verify(discourseUnitHypothesis.spokenByThem!= null);
                Assert.verify(discourseUnitHypothesis.groundInterpretation != null);
                return discourseUnitHypothesis.groundInterpretation.newGetSlotPathFiller("dialogAct").
                        equals(ungroundingAction.getSimpleName());
            }
        }

        public void analyseSuggestions() throws Assert.AssertException {
            if (discourseUnitHypothesis.initiator.equals("user")) {
                Set<String> suggestionPaths = discourseUnitHypothesis.getSpokenByMe().
                        findAllPathsToClass(Suggested.class.getSimpleName());
                Assert.verify(suggestionPaths.size() == 1);
                suggestionPath = new LinkedList<>(suggestionPaths).get(0);
                suggestedContent = (JSONObject) discourseUnitHypothesis.getSpokenByMe().deepCopy().
                        newGetSlotPathFiller(suggestionPath + "." + HasValue.class.getSimpleName());
                groundedSuggestionIndividual = (JSONObject)discourseUnitHypothesis.groundInterpretation.
                        newGetSlotPathFiller(suggestionPath);
            } else { //discourseUnitHypothesis.initiator.equals("system")
                Set<String> suggestionPaths = discourseUnitHypothesis.getSpokenByThem().
                        findAllPathsToClass(Suggested.class.getSimpleName());
                Assert.verify(suggestionPaths.size() == 1);
                suggestionPath = new LinkedList<>(suggestionPaths).get(0);
                suggestedContent = (JSONObject) discourseUnitHypothesis.getSpokenByThem().deepCopy().
                        newGetSlotPathFiller(suggestionPath + "." + HasValue.class.getSimpleName());
                groundedSuggestionIndividual = (JSONObject)discourseUnitHypothesis.groundTruth.
                        newGetSlotPathFiller(suggestionPath);
            }
            descriptionMatch = ReferenceResolution.descriptionMatch(yodaEnvironment,
                    groundedSuggestionIndividual, suggestedContent);
            if (descriptionMatch==null)
                descriptionMatch=0.0;
        }
    }



}
