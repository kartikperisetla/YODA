package edu.cmu.sv.spoken_language_understanding.weighted_rule_tagger;

import java.util.List;

/**
 * Created by David Cohen on 3/27/15.
 */
public interface TaggingRule {
    String applyRuleAtIndex(List<String> tokens, int i);
}
