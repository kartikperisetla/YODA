package edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.Requested;
import edu.cmu.sv.domain.yoda_skeleton.ontology.quality.TransientQuality;
import edu.cmu.sv.domain.yoda_skeleton.ontology.verb.HasProperty;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/24/14.
 */
public class RequestProperty extends DialogAct{
    static Map<String, Class<? extends Thing>> individualParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> classParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> descriptionParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> pathParameters = new HashMap<>();
    @Override
    public Map<String, Class<? extends Thing>> getPathParameters() {
        return pathParameters;
    }

    static{
        classParameters.put("requested_transient_property_class", TransientQuality.class);
        pathParameters.put("given_role_path", Thing.class);
        descriptionParameters.put("given_role_description", Thing.class);
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
    public Map<String, Class<? extends Thing>> getDescriptionParameters() {
        return descriptionParameters;
    }

    @Override
    public Double reward(DialogState dialogState, DiscourseUnit discourseUnit) {
        return RewardAndCostCalculator.requestSlotFillingReward(dialogState, discourseUnit, this);
    }

    @Override
    public SemanticsModel getNlgCommand() {
        SemanticsModel ans = super.getNlgCommand();
        String verbString = "{\"class\":\""+ HasProperty.class.getSimpleName()+"\"}";
        String requestedString = "{\"class\":\""+ Requested.class.getSimpleName()+"\"}";
        ans.getInternalRepresentation().put("verb", SemanticsModel.parseJSON(verbString));
        SemanticsModel.putAtPath(ans.getInternalRepresentation(),
                (String) this.getBoundPaths().get("given_role_path"),
                (JSONObject) this.getBoundDescriptions().get("given_role_description"));
        SemanticsModel.putAtPath(ans.getInternalRepresentation(),
                (String) this.getBoundPaths().get("requested_role_path"),
                SemanticsModel.parseJSON(requestedString));
        return ans;
    }
}
