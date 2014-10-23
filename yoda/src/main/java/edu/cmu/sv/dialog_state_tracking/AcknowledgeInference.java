package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.clarification_dialog_acts.Acknowledge;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.Set;

/**
 * Created by David Cohen on 10/18/14.
 *
 * The AcknowledgeInference re-interprets fragments as confirmations, or makes use of the Acknowledge dialog act.
 * It has the effect of making the speaker say back everything that has been said or suggested by the other speaker.
 */
public class AcknowledgeInference implements DiscourseUnitUpdateInference {
    static Double penaltyForReinterpretingFragment = .5;

    @Override
    public DiscourseUnit2 applyAll(DiscourseUnit2.DialogStateHypothesis currentState, Turn turn, float timeStamp) {
        int newDUHypothesisCounter = 0;
        DiscourseUnit2 ans = new DiscourseUnit2();

        // if the opposite speaker has not yet said anything in this DU,
        // the AcknowledgeInference doesn't make sense
        if ((turn.speaker.equals("user") && currentState.timeOfLastActByMe==null) ||
                (turn.speaker.equals("system") && currentState.timeOfLastActByThem==null))
            return ans;

        if (turn.speaker.equals("user")){
            // find any suggestions, these will all be unwrapped
            Set<String> suggestionPaths = currentState.getSpokenByMe().findAllPathsToClass(Suggested.class.getSimpleName());

            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()){
                SemanticsModel hypModel = turn.hypotheses.get(sluHypothesisID);
                String dialogAct = hypModel.getSlotPathFiller("dialogAct");

                if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Acknowledge.class)) {
                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    DiscourseUnit2.DialogStateHypothesis newDUHypothesis =
                            new DiscourseUnit2.DialogStateHypothesis();
                    SemanticsModel newSpokenByThemHypothesis = currentState.getSpokenByMe().deepCopy();
                    for (String acceptancePath: suggestionPaths) {
                        SemanticsModel.unwrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(acceptancePath),
                                HasValue.class.getSimpleName());
                    }
                    ans.getHypothesisDistribution().put(newDUHypothesisID, 1.0);
                    newDUHypothesis.timeOfLastActByMe = currentState.timeOfLastActByMe;
                    newDUHypothesis.setSpokenByMe(currentState.spokenByMe.deepCopy());
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);

                } else if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Fragment.class)){
                    // Don't currently support interpreting fragments which are conjunctions,
                    // since they correspond to different dialog acts
                    if ("Or".equals(hypModel.newGetSlotPathFiller("topic.class")) ||
                            "And".equals(hypModel.newGetSlotPathFiller("topic.class")))
                        continue;

                    // a single fragment can only be a confirmation for a single suggestion
                    if (suggestionPaths.size() != 1)
                        continue;
                    String suggestionPath = new LinkedList<>(suggestionPaths).get(0);
                    JSONObject daContent = (JSONObject) hypModel.newGetSlotPathFiller("topic");

                    // what is being confirmed must not conflict with what has been suggested
                    if (Utils.anySenseConflicts(
                            (JSONObject) hypModel.newGetSlotPathFiller(suggestionPath + "." + HasValue.class.getSimpleName()), daContent))
                        continue;


                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    DiscourseUnit2.DialogStateHypothesis newDUHypothesis =
                            new DiscourseUnit2.DialogStateHypothesis();
                    SemanticsModel newSpokenByThemHypothesis = currentState.getSpokenByMe().deepCopy();
                    // unwrap the suggestion
                    SemanticsModel.unwrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(suggestionPath),
                            HasValue.class.getSimpleName());
                    // deal with potential over-answering by extending the suggestion with the new content
                    newSpokenByThemHypothesis.extendAndOverwriteAtPoint(suggestionPath,
                            new SemanticsModel(daContent.toJSONString()));

                    ans.getHypothesisDistribution().put(newDUHypothesisID, penaltyForReinterpretingFragment);
                    newDUHypothesis.timeOfLastActByMe = currentState.timeOfLastActByMe;
                    newDUHypothesis.setSpokenByMe(currentState.spokenByMe.deepCopy());
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);
                }
            }
        } else { // if turn.speaker.equals("system")
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            Set<String> suggestionPaths = currentState.spokenByMe.findAllPathsToClass(Suggested.class.getSimpleName());

            if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Acknowledge.class)) {
                String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                DiscourseUnit2.DialogStateHypothesis newDUHypothesis =
                        new DiscourseUnit2.DialogStateHypothesis();
                SemanticsModel newSpokenByMeHypothesis = currentState.getSpokenByThem().deepCopy();
                for (String acceptancePath: suggestionPaths) {
                    SemanticsModel.unwrap((JSONObject) newSpokenByMeHypothesis.newGetSlotPathFiller(acceptancePath),
                            HasValue.class.getSimpleName());
                }
                ans.getHypothesisDistribution().put(newDUHypothesisID, 1.0);
                newDUHypothesis.timeOfLastActByThem = currentState.timeOfLastActByThem;
                newDUHypothesis.setSpokenByThem(currentState.spokenByThem.deepCopy());
                newDUHypothesis.timeOfLastActByMe = timeStamp;
                newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
                ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);
            }
        }
        return ans;
    }
}
