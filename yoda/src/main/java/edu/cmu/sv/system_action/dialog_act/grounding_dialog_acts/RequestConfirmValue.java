package edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class RequestConfirmValue extends ClarificationDialogAct {
    static Map<String, Class<? extends Thing>> individualParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> classParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> descriptionParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> pathParameters = new HashMap<>();
    @Override
    public Map<String, Class<? extends Thing>> getPathParameters() {
        return pathParameters;
    }
    @Override
    public Map<String, Class<? extends Thing>> getDescriptionParameters() {
        return descriptionParameters;
    }
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

    @Override
    public Double clarificationReward(StringDistribution dialogStateDistribution, Map<String, DialogState> dialogStateHypotheses) {
        Double clarificationReward = RewardAndCostCalculator.heuristicClarificationReward(
                dialogStateDistribution, dialogStateHypotheses, (String) getBoundIndividuals().get("topic_individual"));
        Double probabilityOutstandingGroundingRequest =
                RewardAndCostCalculator.outstandingGroundingRequest(dialogStateDistribution, dialogStateHypotheses, "user");
        return (1-probabilityOutstandingGroundingRequest) * clarificationReward;
    }

    @Override
    public SemanticsModel getNlgCommand() {
        SemanticsModel ans = super.getNlgCommand();
        String topicString = OntologyRegistry.webResourceWrap((String) this.getBoundIndividuals().get("topic_individual"));
        ans.getInternalRepresentation().put("topic", SemanticsModel.parseJSON(topicString));
        return ans;
    }
}