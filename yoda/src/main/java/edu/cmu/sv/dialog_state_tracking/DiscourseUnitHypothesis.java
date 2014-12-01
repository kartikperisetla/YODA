package edu.cmu.sv.dialog_state_tracking;

import edu.cmu.sv.database.dialog_task.DialogTask;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

import java.util.Map;

/**
 * Created by David Cohen on 9/17/14.
 */
public class DiscourseUnitHypothesis {
    SemanticsModel spokenByMe;
    SemanticsModel spokenByThem;
    Long timeOfLastActByThem;
    Long timeOfLastActByMe;
    String initiator;
    SemanticsModel groundTruth; // if self-initiated
    SemanticsModel groundInterpretation; // if other-initiated

    // analysis for argumentative purposes
    Map<String, Double> ynqTruth;
    Map<String, Map<String, Double>> whqTruth;

    public void groundAndAnalyse(YodaEnvironment yodaEnvironment){
        String dialogActString = (String) spokenByThem.newGetSlotPathFiller("dialogAct");
        Class<? extends DialogTask> taskClass = DialogRegistry.dialogTaskMap.
                get(DialogRegistry.dialogActNameMap.get(dialogActString));
        try {
            groundTruth = taskClass.newInstance().ground(this, yodaEnvironment);
            taskClass.newInstance().analyse(groundTruth, yodaEnvironment);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public DiscourseUnitHypothesis deepCopy(){
        DiscourseUnitHypothesis ans = new DiscourseUnitHypothesis();
        ans.spokenByMe = spokenByMe.deepCopy();
        ans.spokenByThem = spokenByThem.deepCopy();
        ans.timeOfLastActByMe = timeOfLastActByMe;
        ans.timeOfLastActByThem = timeOfLastActByThem;
        ans.initiator = initiator;
        ans.groundTruth = groundTruth.deepCopy();
        ans.groundInterpretation = groundInterpretation.deepCopy();
        return ans;
    }

}
