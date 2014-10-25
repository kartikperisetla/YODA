package edu.cmu.sv.dialog_state_tracking;

import com.google.common.collect.Iterables;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;
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
    /*
     * Finds the set of paths where the insertion content can be inserted without (?class) conflict,
     * and returns a probability for each insertion point.
     *
     * This will be used by various DST inference classes to attach new information to the dialog state
     *
     * Right now, this is just based on the classes of the insertionPoint and the attachment point
     *
     * insertionContent must be a ThingWithRoles descriptor
     * */
    public static Map<String, Double> findPossiblePointsOfAttachment(SemanticsModel dialogStateSM,
                                                                     JSONObject insertionContent){
        Map<String, Double> ans = new HashMap<>();
        Class <? extends Thing> insertionClass = OntologyRegistry.thingNameMap.
                get((String) insertionContent.get("class"));

//        System.out.println("Utils.findPossiblePointsOfAttachment: insertionContent:"+insertionContent);

        for (String slotPath : dialogStateSM.getAllInternalNodePaths()){
            // if this node isn't a Thing description node, ignore it
            if (!dialogStateSM.getSlotsAtPath(slotPath).contains("class"))
                continue;
            //
            Class <? extends Thing> attachmentPointClass = OntologyRegistry.thingNameMap.
                    get((String) dialogStateSM.newGetSlotPathFiller(slotPath + ".class"));

            Set<Class <? extends Role>> rolesAtPoint = new HashSet<>();
            for (Object key : Iterables.concat(dialogStateSM.getSlotsAtPath(slotPath), insertionContent.keySet())){
                if (!key.equals("class"))
                    rolesAtPoint.add(OntologyRegistry.roleNameMap.get((String)key));
            }

            if (!OntologyRegistry.existsAClassInRangeOfAll(rolesAtPoint))
                continue;

            // allow attachment if there is class compatibility
            if (insertionClass.isAssignableFrom(attachmentPointClass)){
                ans.put(slotPath, 1.0);
            } else if (attachmentPointClass.isAssignableFrom(insertionClass)){
                ans.put(slotPath, 1.0);
            } else if (attachmentPointClass==UnknownThingWithRoles.class){
                ans.put(slotPath, 1.0);
            } else if (insertionClass==UnknownThingWithRoles.class){
                ans.put(slotPath, 1.0);
            }
        }
        return ans;
    }


}
