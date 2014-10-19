package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.ontology.misc.Suggested;
import edu.cmu.sv.ontology.role.HasValue;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.dialog_act.clarification_dialog_acts.RequestConfirmValue;
import edu.cmu.sv.system_action.dialog_act.clarification_dialog_acts.RequestDisambiguateValues;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Fragment;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/18/14.
 *
 * The AcceptInference re-interprets fragments as confirmations, or makes use of the Accept dialog act.
 * It has the effect of making the speaker say back everything that has been said or suggested by the other speaker
 */
public class AcceptInference implements DiscourseUnitUpdateInference {
    static Double penaltyForReinterpretingFragment = .5;

    @Override
    public DiscourseUnit2 applyAll(DiscourseUnit2.DialogStateHypothesis currentState, Turn turn, float timeStamp) {
        int newDUHypothesisCounter = 0;
        DiscourseUnit2 ans = new DiscourseUnit2();

        // if the opposite speaker has not yet said anything in this DU,
        // the AcceptInference doesn't make sense
        if ((turn.speaker.equals("user") && currentState.timeOfLastActByMe==null) ||
                (turn.speaker.equals("system") && currentState.timeOfLastActByThem==null))
            return ans;

        if (turn.speaker.equals("user")){
            for (String sluHypothesisID : turn.hypothesisDistribution.keySet()){
                SemanticsModel hypModel = turn.hypotheses.get(sluHypothesisID);
                String dialogAct = hypModel.getSlotPathFiller("dialogAct");

                // find any suggestions, these will all be unwrapped
                Set<String> acceptancePaths = hypModel.findAllPathsToClass(Suggested.class.getSimpleName());

                if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Accept.class)) {

                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    DiscourseUnit2.DialogStateHypothesis newDUHypothesis =
                            new DiscourseUnit2.DialogStateHypothesis();
                    SemanticsModel newSpokenByThemHypothesis = currentState.getSpokenByThem().deepCopy();
                    for (String acceptancePath: acceptancePaths) {
                        SemanticsModel.unwrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(acceptancePath),
                                HasValue.class.getSimpleName());
                    }
                    ans.getHypothesisDistribution().put(newDUHypothesisID, 1.0);
                    newDUHypothesis.timeOfLastActByThem = timeStamp;
                    newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                    ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);

                } else if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(Fragment.class)){
                    //TODO: implement this case
                    //TODO: if the given fragment doesn't agree with anything suggested, this should be interpreted as a correction, not an acceptance
                    //TODO: if the given fragment does agree with the suggested value(s?), unwrap them in the result
                    //TODO: how to deal with overanswering?
                    // Don't currently support interpreting fragments which are conjunctions,
                    // since they correspond to different dialog acts
                    if ("Or".equals(hypModel.newGetSlotPathFiller("topic.class")) ||
                            "And".equals(hypModel.newGetSlotPathFiller("topic.class")))
                        continue;

                    JSONObject daContent = (JSONObject) hypModel.newGetSlotPathFiller("topic");

                    Map<String, Double> attachmentPoints = Utils.findPossiblePointsOfAttachment(
                            currentState.getSpokenByThem(), daContent);
                    SemanticsModel wrapped = new SemanticsModel(daContent.toJSONString());
                    SemanticsModel.wrap((JSONObject) wrapped.newGetSlotPathFiller(""),
                            Suggested.class.getSimpleName(), HasValue.class.getSimpleName());

                    for (String attachmentPoint : attachmentPoints.keySet()){
                        String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                        DiscourseUnit2.DialogStateHypothesis newDUHypothesis =
                                new DiscourseUnit2.DialogStateHypothesis();
                        SemanticsModel newSpokenByThemHypothesis = currentState.getSpokenByThem().deepCopy();
                        newSpokenByThemHypothesis.extendAndOverwriteAtPoint(attachmentPoint, wrapped);
                        SemanticsModel.wrap((JSONObject) newSpokenByThemHypothesis.newGetSlotPathFiller(attachmentPoint),
                                Suggested.class.getSimpleName(), HasValue.class.getSimpleName());
                        ans.getHypothesisDistribution().put(newDUHypothesisID, attachmentPoints.get(attachmentPoint) *
                                penaltyForReinterpretingFragment);
                        newDUHypothesis.timeOfLastActByThem = timeStamp;
                        newDUHypothesis.spokenByThem = newSpokenByThemHypothesis;
                        ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);
                    }
                }
            }
        } else { // if turn.speaker.equals("system")
            // todo: implement this case
            String dialogAct = turn.systemUtterance.getSlotPathFiller("dialogAct");

            if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(RequestConfirmValue.class)){
                JSONObject daContent = (JSONObject) turn.systemUtterance.newGetSlotPathFiller("topic");
                Map<String, Double> attachmentPoints = Utils.findPossiblePointsOfAttachment(
                        currentState.getSpokenByThem(), daContent);
                SemanticsModel wrapped = new SemanticsModel(daContent.toJSONString());
                SemanticsModel.wrap((JSONObject) wrapped.newGetSlotPathFiller(""),
                        Suggested.class.getSimpleName(), HasValue.class.getSimpleName());

                for (String attachmentPoint : attachmentPoints.keySet()){
                    String newDUHypothesisID = "du_hyp_" + newDUHypothesisCounter++;
                    DiscourseUnit2.DialogStateHypothesis newDUHypothesis =
                            new DiscourseUnit2.DialogStateHypothesis();
                    SemanticsModel newSpokenByMeHypothesis = currentState.getSpokenByThem().deepCopy();
                    SemanticsModel.wrap((JSONObject)newSpokenByMeHypothesis.newGetSlotPathFiller(attachmentPoint),
                            Suggested.class.getSimpleName(), HasValue.class.getSimpleName());
                    newSpokenByMeHypothesis.extendAndOverwriteAtPoint(attachmentPoint, wrapped);
                    ans.getHypothesisDistribution().put(newDUHypothesisID, attachmentPoints.get(attachmentPoint));
                    newDUHypothesis.timeOfLastActByMe = timeStamp;
                    newDUHypothesis.spokenByMe = newSpokenByMeHypothesis;
                    ans.hypotheses.put(newDUHypothesisID, newDUHypothesis);
                }
            } else if (DialogRegistry.dialogActNameMap.get(dialogAct).equals(RequestDisambiguateValues.class)) {
                throw new Error("Not yet implemented: SuggestedInference for RequestDisambiguateValues dialogAct");
            }

        }
        return ans;    }
}
