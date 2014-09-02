package edu.cmu.sv.semantics;

import java.util.*;

/**
 * Created by David Cohen on 8/27/14.
 *
 * A hierarchical slot-filling model. Used to represent utterance / discourse unit meaning.
 *
 */
public class SemanticsModel {
    Map<String, String> slots;
    Map<String, SemanticsModel> children;

    /*
    * This may return a slot value, or some local identifier for a child semantics model, or null
    * */
    public String getSlotPathFiller(String slotPath){
        String[] fillerPath = slotPath.split("\\.");
        SemanticsModel tmp = this;
        // follow the chain to the last semantics model
        for (int i = 0; i < fillerPath.length-1; i++) {
            if (tmp.slots.containsKey(fillerPath[i]) && tmp.children.containsKey(tmp.slots.get(fillerPath[i])))
                tmp = tmp.children.get(tmp.slots.get(fillerPath[i]));
            else
                return null;
        }
        // return the last filler (which may be null)
        return tmp.slots.get(fillerPath[fillerPath.length-1]);
    }

    public Map<String, Set<String>> getAllSlotFillers(){
        Map<String, Set<String>> ans = new HashMap<>();
        // collect top level slots/fillers
        for (String slot : slots.keySet()){
            ans.put(slot, new HashSet<>(Arrays.asList(slots.get(slot))));
        }
        // collect recursively
        for (String childID : children.keySet()){
            Map<String, Set<String>> childSlotFillers = children.get(childID).getAllSlotFillers();
            for (String key : childSlotFillers.keySet()){
                ans.put(childID+"."+key, childSlotFillers.get(key));
            }
        }
        return ans;
    }

    public Map<String, String> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, String> slots) {
        this.slots = slots;
    }

    public Map<String, SemanticsModel> getChildren() {
        return children;
    }

    public void setChildren(Map<String, SemanticsModel> children) {
        this.children = children;
    }
}
