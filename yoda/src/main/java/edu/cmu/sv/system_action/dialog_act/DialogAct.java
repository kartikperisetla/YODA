package edu.cmu.sv.system_action.dialog_act;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.system_action.SystemAction;

import java.util.*;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Define the illocutionary acts for the YODA dialog system
 *
 * Define information to support decision-making by the dialog manager
 *
 * Possibly in the future:
 *   - include templates used for NLG and SLU
 *
 */
public abstract class DialogAct extends SystemAction {
    public abstract Double reward(DiscourseUnit2 DU);
    public abstract Double cost(DiscourseUnit2 DU);
    public abstract Map<String, Class<? extends Thing>> getParameters();
    public abstract Map<String, Object> getBindings();
    public abstract void bindVariables(Map<String, Object> bindings);

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+ "{" +
                "boundVariables=" + getBindings() +
                '}';
    }

}
