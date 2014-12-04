package edu.cmu.sv.dialog_state_tracking;

import com.google.common.collect.Iterables;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/17/14.
 */
public class Utils {

    public static int numberOfIntermediateDiscourseUnitsBySpeaker(DiscourseUnitHypothesis predecessorDu,
                                                           DialogStateHypothesis dialogStateHypothesis, String speaker){
        int ans = 0;
        for (String discourseUnitIdentifier : dialogStateHypothesis.getDiscourseUnitHypothesisMap().keySet()){
            DiscourseUnitHypothesis otherPredecessor = dialogStateHypothesis.getDiscourseUnitHypothesisMap().get(discourseUnitIdentifier);
            if (otherPredecessor==predecessorDu)
                continue;
            if (otherPredecessor.getInitiator().equals(speaker) &&
                    otherPredecessor.getMostRecentContributionTime() > predecessorDu.getMostRecentContributionTime())
                ans += 1;
        }
        return ans;
    }


}
