package edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/18/14.
 *
 * A repair-type clarification Dialog act to correct some content which was suggested
 * by the other speaker as a request-repair, but was incorrect.
 *
 * Ex:
 * Present: "4"
 * Req. Conf. : "3?"
 * Correct: "No"
 *
 * This does not confirm cases where the user has suggested additional information.
 */
public class DenyIncorrectSenseSuggestion extends DialogAct {
    static Map<String, Class<? extends Thing>> individualParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> classParameters = new HashMap<>();
    static{
        individualParameters.put("topic_individual", Noun.class);
    }
    @Override
    public Map<String, Class<? extends Thing>> getClassParameters() {
        return classParameters;
    }
    @Override
    public Map<String, Class<? extends Thing>> getIndividualParameters() {
        return individualParameters;
    }

    /*
    * If there are bindings for suggestion,
    * and the suggestion matches what the system has already said,
    * there is a high penalty for not confirming.
    * */
    @Override
    public Double reward(DiscourseUnit2 DU) {
        // p(there is an outstanding suggestion) *
        // p(outstanding suggestion is correct | there is a suggestion) *
        // penalty for ignoring discourse obligations
        Double ans = 0.0;
        for (String hypID : DU.getHypotheses().keySet()){
            Set<String> pathsToSuggestions = DU.getHypotheses().get(hypID).getSpokenByThem().
                    findAllPathsToClass(Suggested.class.getSimpleName());
            // verify that the other speaker has made a suggestion (and only one)
            if (pathsToSuggestions.size()!=1)
                continue;
            JSONObject suggestedContent = (JSONObject) DU.getHypotheses().get(hypID).
                    getSpokenByThem().
                    newGetSlotPathFiller(new LinkedList<>(pathsToSuggestions).get(0)+"."+ HasValue.class.getSimpleName());
            Object correspondingPresentedContent = DU.getHypotheses().get(hypID).
                    getSpokenByMe().
                    newGetSlotPathFiller(new LinkedList<>(pathsToSuggestions).get(0));
            // verify that the suggestion agrees with what has been presented by the system
            if (correspondingPresentedContent==null)
                continue;
            if (correspondingPresentedContent instanceof String)
                continue;
            if (!SemanticsModel.anyNewSenseInformation((JSONObject) correspondingPresentedContent,
                    suggestedContent))
                continue;
            // collect probability
            ans += RewardAndCostCalculator.penaltyForIgnoringUserRequest *
                    DU.getHypothesisDistribution().get(hypID);
        }
        ans -= RewardAndCostCalculator.penaltyForSpeakingPhrase;
        return ans;
    }

}
