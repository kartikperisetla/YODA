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

    public SemanticsModel() {
        slots = new HashMap<>();
        children = new HashMap<>();
    }

    public SemanticsModel deepCopy(){
        SemanticsModel ans = new SemanticsModel();
        for (String key : slots.keySet())
            ans.getSlots().put(key, slots.get(key));
        for (String key : children.keySet())
            ans.getChildren().put(key, children.get(key).deepCopy());
        return ans;
    }

    /*
    * Extend this model with other, overwriting values as necessary
    * */
    public void extend(SemanticsModel other){
        // pointer map maps other.ptr -> this.ptr for equivalent pointers
        Map<String, String> pointerMap = new HashMap<>();
        for (String otherKey : other.slots.keySet()){
            String otherValue = other.slots.get(otherKey);
            String currentValue = slots.get(otherKey);
            if (other.children.containsKey(otherValue)){
                if (null==currentValue){
                    children.put(otherValue, other.children.get(otherValue).deepCopy());
                    slots.put(otherKey, other.slots.get(otherKey));
                } else {
                    children.get(currentValue).extend(other.children.get(otherValue));
                }
            } else {
                slots.put(otherKey, other.slots.get(otherKey));
            }
        }
    }


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

    public Map<String, String> getAllNonSpecialSlotFillerLeafPairs(){
        Map<String, String> ans = new HashMap<>();
        // collect top level slots/fillers
        // (exclude non-leaf pairs and special values (marked by surrounding arrow brackets))
        for (String slot : slots.keySet()){
            if (!children.containsKey(slots.get(slot)) && !slots.get(slot).matches("\\<.*\\>"))
                ans.put(slot, slots.get(slot));
        }
        // collect recursively
        for (String slot : slots.keySet()){
            if (!children.containsKey(slots.get(slot)))
                continue;
            Map<String, String> childSlotFillers = children.get(slots.get(slot)).getAllNonSpecialSlotFillerLeafPairs();
            for (String key : childSlotFillers.keySet()){
                ans.put(slot+"."+key, childSlotFillers.get(key));
            }
        }
        return ans;
    }

    public Map<String, String> getSlots() {
        return slots;
    }

    public Map<String, SemanticsModel> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemanticsModel that = (SemanticsModel) o;

        if (children != null ? !children.equals(that.children) : that.children != null) return false;
        if (slots != null ? !slots.equals(that.slots) : that.slots != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = slots != null ? slots.hashCode() : 0;
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String ans = "";
        boolean started = false;
        for (String slot: slots.keySet()){
            if (children.containsKey(slots.get(slot))){
                if (started)
                    ans += "\n"+ slot;
                else
                    ans += slot;
                ans += children.get(slots.get(slot)).toString().replaceAll("\\n", "\n    ");
            } else {
                if (started)
                    ans += "\n"+slot + " -> " + slots.get(slot);
                else
                    ans += slot + " -> " + slots.get(slot);
            }
            started = true;
        }
        return ans.trim();
    }
}
