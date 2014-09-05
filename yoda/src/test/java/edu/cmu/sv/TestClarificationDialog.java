package edu.cmu.sv;

import edu.cmu.sv.dialog_management.DialogManager;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 9/4/14.
 */
public class TestClarificationDialog {

    @Test
    public void Test(){
        DialogManager dialogManager = new DialogManager();

        //// The following example should give high scores to dialog acts that
        // disambiguate between y and z
        // or that ask confirm y, or that ask confirm z
        Map<String, SemanticsModel> utterances = new HashMap<>();
        StringDistribution weights = new StringDistribution();

        SemanticsModel hyp1 = new SemanticsModel();
        hyp1.getSlots().put("dialogAct", "WHQuestion");
        hyp1.getSlots().put("slot1", "x");
        hyp1.getSlots().put("slot2", "y");
        utterances.put("hyp1", hyp1);
        weights.extend("hyp1", .6);

        SemanticsModel hyp2 = new SemanticsModel();
        hyp2.getSlots().put("dialogAct", "WHQuestion");
        hyp2.getSlots().put("slot1", "x");
        hyp2.getSlots().put("slot2", "z");
        utterances.put("hyp2", hyp2);
        weights.extend("hyp2", .6);

        dialogManager.getTracker().updateDialogState(utterances, weights, (float)0);
        dialogManager.evaluateClarificationActions();
    }


}
