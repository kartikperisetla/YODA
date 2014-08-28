package edu.cmu.sv.semantics;

import java.util.Map;

/**
 * Created by David Cohen on 8/27/14.
 *
 * A hierarchical slot-filling model. Used to represent utterance / discourse unit meaning.
 *
 */
public class SemanticsModel {
    Map<String, String> slots;
    Map<String, SemanticsModel> children;

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
