package edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts;

import edu.cmu.sv.dialog_management.RewardAndCostCalculator;
import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.domain.ontology2.Quality2;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/24/14.
 */
public class RequestProperty extends DialogAct{
    static Map<String, Object> individualParameters = new HashMap<>();
    static Map<String, Object> classParameters = new HashMap<>();
    static Map<String, Object> descriptionParameters = new HashMap<>();
    static Map<String, Object> pathParameters = new HashMap<>();
    @Override
    public Map<String, Object> getPathParameters() {
        return pathParameters;
    }

    static{
        classParameters.put("requested_transient_property_class", Quality2.class);
        pathParameters.put("given_role_path", Object.class);
        descriptionParameters.put("given_role_description", Object.class);
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
    public Map<String, Object> getDescriptionParameters() {
        return descriptionParameters;
    }

    @Override
    public Double reward(DialogState dialogState, DiscourseUnit discourseUnit) {
        return RewardAndCostCalculator.requestSlotFillingReward(dialogState, discourseUnit, this);
    }

    @Override
    public SemanticsModel getNlgCommand() {
        SemanticsModel ans = super.getNlgCommand();
        String verbString = "{\"class\":\""+ YodaSkeletonOntologyRegistry.hasProperty.name+"\"}";
        String requestedString = "{\"class\":\""+ YodaSkeletonOntologyRegistry.requested.name+"\"}";
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
