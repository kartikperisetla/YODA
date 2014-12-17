package edu.cmu.sv.system_action.dialog_act.grounding_dialog_acts;

import edu.cmu.sv.dialog_state_tracking.DialogStateHypothesis;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.SystemAction;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.utils.StringDistribution;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/2/14.
 *
 * Define the dialog acts for the YODA dialog system
 * Define information to support decision-making by the dialog manager, and to generate NLG input
 *
 * For class bindings, bindings are bound as Class objects
 * For individual bindings, bindings are bound as URIs
 *
 */
public abstract class ClarificationDialogAct extends DialogAct {

    @Override
    public Double reward(DialogStateHypothesis dialogStateHypothesis,
                         DiscourseUnitHypothesis discourseUnitHypothesis) {
        return null;
    }

    public abstract Double clarificationReward(StringDistribution dialogStateDistribution,
                                               Map<String, DialogStateHypothesis> dialogStateHypotheses);

}
