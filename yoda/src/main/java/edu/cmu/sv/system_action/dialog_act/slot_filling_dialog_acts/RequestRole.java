package edu.cmu.sv.system_action.dialog_act.slot_filling_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DialogState;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.Requested;
import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/24/14.
 */
public class RequestRole extends DialogAct{
    static Map<String, Class<? extends Thing>> individualParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> classParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> descriptionParameters = new HashMap<>();
    static Map<String, Class<? extends Thing>> pathParameters = new HashMap<>();
    @Override
    public Map<String, Class<? extends Thing>> getPathParameters() {
        return pathParameters;
    }

    static{
        classParameters.put("verb_class", Verb.class);
        pathParameters.put("requested_role_path", Thing.class);
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
        return 0.0;
    }

    @Override
    public SemanticsModel getNlgCommand() {
        SemanticsModel ans = super.getNlgCommand();
        String verbString = "{\"class\":\""+this.getBoundClasses().get("verb_class")+"\"}";
        String requestedString = "{\"class\":\""+ Requested.class.getSimpleName()+"\"}";
        ans.getInternalRepresentation().put("verb", SemanticsModel.parseJSON(verbString));
        ans.getInternalRepresentation().put(this.getBoundPaths().get("given_role_path"),
                this.getBoundDescriptions().get("given_role_description"));
        SemanticsModel.putAtPath(ans.getInternalRepresentation(),
                (String) this.getBoundPaths().get("requested_role_path"),
                SemanticsModel.parseJSON(requestedString));
        return ans;
    }
}
