package edu.cmu.sv.dialog_state_tracking;

import com.google.common.collect.Iterables;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
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

    /*
    * Rank the possible places where the suggestion content might attach to the existing semantics model
    * */
    public static StringDistribution findPossiblePointsOfAttachment(SemanticsModel discourseUnitSemantics,
                                                                    /*SemanticsModel groundedSemantics,*/
                                                                    JSONObject suggestionContent){
        StringDistribution ans = new StringDistribution();
        Set<Object> verbRoles = ((JSONObject) discourseUnitSemantics.newGetSlotPathFiller("verb")).keySet();
        String contentClass = (String) suggestionContent.get("class");
        if (contentClass.equals(UnknownThingWithRoles.class.getSimpleName())){
            for (Object key : verbRoles){
                if (key.equals("class"))
                    continue;
                JSONObject filler = (JSONObject) discourseUnitSemantics.newGetSlotPathFiller("verb."+key);
                if (filler.get("class").equals(UnknownThingWithRoles.class.getSimpleName()))
                    ans.put("verb."+key, 1.0);
            }
        } else if (Noun.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(contentClass))) {
            for (Object key : verbRoles) {
                if (key.equals("class"))
                    continue;
                JSONObject filler = (JSONObject) discourseUnitSemantics.newGetSlotPathFiller("verb." + key);
                if (filler.get("class").equals(contentClass))
                    ans.put("verb." + key, 1.0);
                else if (OntologyRegistry.thingNameMap.get(contentClass).isAssignableFrom(OntologyRegistry.thingNameMap.get(filler.get("class"))) ||
                        OntologyRegistry.thingNameMap.get(filler.get("class")).isAssignableFrom(OntologyRegistry.thingNameMap.get(contentClass)))
                    ans.put("verb." + key, 1.0);
                else if (Noun.class.isAssignableFrom(OntologyRegistry.thingNameMap.get(filler.get("class"))))
                    ans.put("verb." + key, .5);
            }
        }
        return ans;
    }

}
