package edu.cmu.sv.database.dialog_task;

import edu.cmu.sv.dialog_state_tracking.DiscourseUnitHypothesis;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * Created by David Cohen on 9/3/14.
 *
 * A DialogTask defines a reference resolution and analysis procedure for different dialogAct/verb combinations
 */
public abstract class DialogTask{
    SemanticsModel taskSpec;

    public void setTaskSpec(SemanticsModel taskSpec){
        this.taskSpec = taskSpec;
    }
    public SemanticsModel getTaskSpec(){
        return taskSpec;
    }

    public abstract Pair<Map<String, DiscourseUnitHypothesis>, StringDistribution> groundAndAnalyse(
           DiscourseUnitHypothesis hypothesis, YodaEnvironment yodaEnvironment);
}
