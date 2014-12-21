package edu.cmu.sv.system_action;

import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.natural_language_generation.Grammar;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.dialog_act.DialogAct;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.simple.JSONObject;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by David Cohen on 12/21/14.
 *
 * Executor object is used to manage the execution of system actions.
 * Sends feedback to dialog state tracker.
 *
 */
public class Executor {
    YodaEnvironment yodaEnvironment;

    public Executor(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    public void execute(SystemAction systemAction){
        if (systemAction instanceof DialogAct){
            SemanticsModel model = ((DialogAct) systemAction).getNlgCommand();

            NaturalLanguageGenerator.getLogger().info("nlg request made:"+model);
            Map.Entry<String, SemanticsModel> chosenUtterance =
                    yodaEnvironment.nlg.generateBestForSemantics(model,
                            Grammar.DEFAULT_GRAMMAR_PREFERENCES);
            chosenUtterance.getValue().filterOutLeafSlot("chunk-start");
            chosenUtterance.getValue().filterOutLeafSlot("chunk-end");
            NaturalLanguageGenerator.getLogger().info("chosen utterance:" + chosenUtterance);

            yodaEnvironment.out.sendOutput(chosenUtterance.getKey());
            Turn systemTurn = new Turn("system", chosenUtterance.getValue(), model, null, null);
            Calendar calendar = Calendar.getInstance();
            yodaEnvironment.DstInputQueue.add(new ImmutablePair<>(systemTurn, calendar.getTimeInMillis()));

        } else if (systemAction instanceof NonDialogTask){

            JSONObject taskSemantics = SemanticsModel.parseJSON("{\"dialogAct\":\""+systemAction.getClass().getSimpleName()+"\"}");
            taskSemantics.put("verb", SemanticsModel.parseJSON(((NonDialogTask) systemAction).getTaskSpec().toJSONString()));

            SemanticsModel taskSemanticModel = new SemanticsModel(taskSemantics);
            ((NonDialogTask) systemAction).execute();
            Turn systemTurn = new Turn("system", taskSemanticModel.deepCopy(), taskSemanticModel, null, null);

            Calendar calendar = Calendar.getInstance();
            yodaEnvironment.DstInputQueue.add(new ImmutablePair<>(systemTurn, calendar.getTimeInMillis()));

        } else {
            throw new Error("Can not execute this type of action: "+systemAction);
        }


    }

}
