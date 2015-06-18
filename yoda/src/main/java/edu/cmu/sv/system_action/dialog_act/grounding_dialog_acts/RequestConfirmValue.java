package edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts;

import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.NBestDistribution;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class RequestConfirmValue extends ClarificationDialogAct {
    static Map<String, Object> individualParameters = new HashMap<>();
    static Map<String, Object> classParameters = new HashMap<>();
    static Map<String, Object> descriptionParameters = new HashMap<>();
    static Map<String, Object> pathParameters = new HashMap<>();
    @Override
    public Map<String, Object> getPathParameters() {
        return pathParameters;
    }
    @Override
    public Map<String, Object> getDescriptionParameters() {
        return descriptionParameters;
    }
    static{
        individualParameters.put("topic_individual", YodaSkeletonOntologyRegistry.rootNoun);
    }
    @Override
    public Map<String, Object> getClassParameters() {
        return classParameters;
    }
    @Override
    public Map<String, Object> getIndividualParameters() {
        return individualParameters;
    }

    @Override
    public Double clarificationReward(NBestDistribution<DialogState> dialogStateDistribution) {
        Double clarificationReward = RewardAndCostCalculator.heuristicClarificationReward(
                dialogStateDistribution, (String) getBoundIndividuals().get("topic_individual"));
        Double probabilityOutstandingGroundingRequest =
                RewardAndCostCalculator.outstandingGroundingRequest(dialogStateDistribution,"user");
        return (1-probabilityOutstandingGroundingRequest) * clarificationReward;
    }

    @Override
    public SemanticsModel getNlgCommand() {
        SemanticsModel ans = super.getNlgCommand();
        String topicString = Ontology.webResourceWrap((String) this.getBoundIndividuals().get("topic_individual"));
        ans.getInternalRepresentation().put("topic", SemanticsModel.parseJSON(topicString));
        return ans;
    }
}