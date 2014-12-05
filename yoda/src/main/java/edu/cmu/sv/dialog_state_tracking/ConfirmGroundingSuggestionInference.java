package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/18/14.
 */
public class ConfirmGroundingSuggestionInference extends DialogStateUpdateInference {
    static Double penaltyForReinterpretingFragment = .5;


    @Override
    public Pair<Map<String, DialogStateHypothesis>, StringDistribution> applyAll(DialogStateHypothesis currentState, Turn turn, long timeStamp) {
        StringDistribution resultDistribution = new StringDistribution();
        Map<String, DialogStateHypothesis> resultHypotheses = new HashMap<>();

        int newHypothesisCounter = 0;
        if (turn.speaker.equals("user")) {
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()) {
                SemanticsModel hypModel = turn.hypotheses.get(sluHypothesisID);
                String dialogAct = hypModel.getSlotPathFiller("dialogAct");
                if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Accept.class)) {
                    for (String predecessorId : currentState.discourseUnitHypothesisMap.keySet()) {
                        DiscourseUnitHypothesis predecessor = currentState.discourseUnitHypothesisMap.get(predecessorId);
                        if (!predecessor.initiator.equals("user"))
                            continue;
                        Set<String> suggestionPaths = predecessor.getSpokenByMe().findAllPathsToClass(Suggested.class.getSimpleName());
                        // a single fragment can only be a confirmation for a single suggestion
                        if (suggestionPaths.size() != 1)
                            continue;

                        SemanticsModel newSpokenByThemHypothesis = predecessor.getSpokenByThem().deepCopy();
                        newSpokenByThemHypothesis.placeAtPoint("verb",
                                new SemanticsModel(((JSONObject)predecessor.getSpokenByMe().
                                        newGetSlotPathFiller("verb"))).deepCopy());
//                    SemanticsModel newSpokenByThemHypothesis = currentState.getSpokenByMe().deepCopy();
                        for (String acceptancePath: suggestionPaths) {
                            SemanticsModel.unwrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(acceptancePath),
                                    HasValue.class.getSimpleName());
                        }
                        ans.getHypothesisDistribution().put(newDUHypothesisID, 1.0);
                        newDUHypothesis.timeOfLastActByMe = currentState.timeOfLastActByMe;
                        newDUHypothesis.setSpokenByMe(currentState.spokenByMe.deepCopy());
                        newDUHypothesis.timeOfLastActByThem = timeStamp;
                        newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;





                    }
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
                    if (SemanticsModel.anySenseConflicts(
                            (JSONObject) currentState.getSpokenByMe().newGetSlotPathFiller(suggestionPath + "." + HasValue.class.getSimpleName()), daContent))
                        continue;

                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    DiscourseUnit2.DiscourseUnitHypothesis newDUHypothesis =
                            new DiscourseUnit2.DiscourseUnitHypothesis();
                    SemanticsModel newSpokenByThemHypothesis = currentState.getSpokenByThem().deepCopy();
                    newSpokenByThemHypothesis.placeAtPoint("verb",
                            new SemanticsModel(((JSONObject)currentState.getMostRecent().
                                    newGetSlotPathFiller("verb"))).deepCopy());
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

        return new ImmutablePair<>(resultHypotheses, resultDistribution);
    }

        } else { // if turn.speaker.equals("system")
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");
            Set<String> suggestionPaths = currentState.spokenByMe.findAllPathsToClass(Suggested.class.getSimpleName());

            if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(ConfirmSenseSuggestion.class)) {
                String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                DiscourseUnit2.DiscourseUnitHypothesis newDUHypothesis =
                        new DiscourseUnit2.DiscourseUnitHypothesis();
                SemanticsModel newSpokenByMeHypothesis = currentState.getSpokenByMe().deepCopy();
                newSpokenByMeHypothesis.placeAtPoint("verb",
                        new SemanticsModel(((JSONObject) currentState.getMostRecent().
                                newGetSlotPathFiller("verb"))).deepCopy());
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
