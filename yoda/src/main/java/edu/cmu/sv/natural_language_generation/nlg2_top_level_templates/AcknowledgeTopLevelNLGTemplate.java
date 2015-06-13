package edu.cmu.sv.natural_language_generation.nlg2_top_level_templates;

import edu.cmu.sv.natural_language_generation.TopLevelNLGTemplate;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Created by David Cohen on 10/29/14.
 */
public class AcknowledgeTopLevelNLGTemplate implements TopLevelNLGTemplate {
    @Override
    public ImmutablePair<String, SemanticsModel> generate(SemanticsModel constraints, YodaEnvironment yodaEnvironment) {
        return new ImmutablePair<>("Ok", constraints.deepCopy());
    }
}
