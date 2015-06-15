package edu.cmu.sv.natural_language_generation.top_level_templates;

import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.core_dialog_acts.Accept;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by David Cohen on 10/29/14.
 */
public class ConfirmGroundingSuggestionTopLevelNLGTemplate implements TopLevelNLGTemplate {
    @Override
    public ImmutablePair<String, SemanticsModel> generate(SemanticsModel constraints, YodaEnvironment yodaEnvironment) {
        SemanticsModel newConstraints = constraints.deepCopy();
        SemanticsModel.putAtPath(newConstraints.getInternalRepresentation(), "dialogAct", Accept.class.getSimpleName());
        return new ImmutablePair<>("Yes", newConstraints);
    }
}
