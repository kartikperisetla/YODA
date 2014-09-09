package edu.cmu.sv.dialog_management.dialog_act;

import edu.cmu.sv.dialog_management.DiscourseUnit;

import java.util.Map;

/**
 * Created by David Cohen on 9/8/14.
 */
public class WHQuestion implements DialogAct {
    @Override
    public Double reward(DiscourseUnit DU) {
        return null;
    }

    @Override
    public Double cost(DiscourseUnit DU) {
        return null;
    }

    @Override
    public Map<String, String> getParameters() {
        return null;
    }

    @Override
    public Map<String, String> getBindings() {
        return null;
    }

    @Override
    public DialogAct bindVariables(Map<String, String> bindings) {
        return null;
    }
}
