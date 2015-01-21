package edu.cmu.sv.system_action.dialog_act.core_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class Statement extends DialogAct {
    static Map<String, Class<? extends Thing>> individualParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> classParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> descriptionParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> pathParameters = new HashMap<>();
    @Override
    public Map<String, Class<? extends Thing>> getPathParameters() {
        return pathParameters;
    }
    static{
        individualParameters.put("topic_individual", Noun.class);
        classParameters.put("verb_class", Verb.class);
        descriptionParameters.put("asserted_role_description", Thing.class);
    }
    @Override
    public Map<String, Class<? extends Thing>> getDescriptionParameters() {
        return descriptionParameters;
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
    public SemanticsModel getNlgCommand() {
        SemanticsModel ans = super.getNlgCommand();
        ans.getInternalRepresentation().put("verb",
                SemanticsModel.parseJSON("{\"class\":\""+(String)this.getBoundClasses().get("verb_class")+"\"}"));
        String topicString = OntologyRegistry.webResourceWrap((String) this.getBoundIndividuals().get("topic_individual"));
        ((JSONObject)ans.newGetSlotPathFiller("verb")).put("Agent", SemanticsModel.parseJSON(topicString));
        ((JSONObject)ans.newGetSlotPathFiller("verb")).put("Patient",
                SemanticsModel.parseJSON(((JSONObject)this.getBoundDescriptions().get("asserted_role_description")).toJSONString()));
        return ans;
    }

    @Override
    public Double reward(DialogState dialogState, DiscourseUnit discourseUnit) {
        return (RewardAndCostCalculator.discourseIndependentStatementReward(this, discourseUnit) *
                RewardAndCostCalculator.probabilityInterpretedCorrectly(discourseUnit, dialogState, this)) +
                (RewardAndCostCalculator.answerObliged(discourseUnit) &&
                        !RewardAndCostCalculator.answerAlreadyProvided(discourseUnit, dialogState) ?
                        RewardAndCostCalculator.penaltyForIgnoringUserRequest : 0);
    }

}
