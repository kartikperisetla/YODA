package edu.cmu.sv.system_action.dialog_act.core_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.dialog_state_tracking.Utils;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/18/14.
 */
public class RequestSearchAlternative extends DialogAct {
    static Map<String, Object> individualParameters = new HashMap<>();
    static Map<String, Object> classParameters = new HashMap<>();
    static Map<String, Object> descriptionParameters = new HashMap<>();
    static Map<String, Object> pathParameters = new HashMap<>();

    static{
        descriptionParameters.put("asserted_role_description", Thing.class);
        classParameters.put("verb_class", Verb.class);
    }

    @Override
    public Map<String, Object> getPathParameters() {
        return pathParameters;
    }
    @Override
    public Map<String, Object> getDescriptionParameters() {
        return descriptionParameters;
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
    public SemanticsModel getNlgCommand() {
        SemanticsModel ans = super.getNlgCommand();
        ans.getInternalRepresentation().put("verb",
                SemanticsModel.parseJSON("{\"class\":\""+this.getBoundClasses().get("verb_class")+"\"}"));
        ((JSONObject)ans.newGetSlotPathFiller("verb")).put("Patient",
                SemanticsModel.parseJSON(((JSONObject)this.getBoundDescriptions().get("asserted_role_description")).toJSONString()));
        return ans;
    }

    @Override
    public Double reward(DialogState dialogState, DiscourseUnit discourseUnit) {
        double statementReward = RewardAndCostCalculator.discourseIndependentStatementReward(this, discourseUnit);
        double probabilityInterpretedCorrectly = Utils.discourseUnitContextProbability(dialogState, discourseUnit);
        boolean answerObliged = RewardAndCostCalculator.answerObliged(discourseUnit);
        boolean answerNotProvided = !RewardAndCostCalculator.answerAlreadyProvided(discourseUnit, dialogState);

//        System.out.println("Statement reward: " + statementReward +
//                ", probability interpreted correctly:" + probabilityInterpretedCorrectly +
//        ", answer obliged:" + answerObliged + ", answer not provided:" + answerNotProvided);
        double ans = ( statementReward * probabilityInterpretedCorrectly) +
                (answerObliged && answerNotProvided ? RewardAndCostCalculator.penaltyForIgnoringUserRequest : 0);
//        System.out.println("Statement reward:" + ans);
        return ans;



    }
}
