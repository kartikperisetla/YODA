package edu.cmu.sv.system_action;

import edu.cmu.sv.ontology.verb.Verb;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.system_action.non_dialog_task.NonDialogTask;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 12/19/14.
 */
public class GenericCommandSchema extends ActionSchema {
    private Class<? extends Verb> verbClass;
    private Class<? extends NonDialogTask> taskClass;

    public GenericCommandSchema(Class<? extends Verb> verbClass, Class<? extends NonDialogTask> taskClass) {
        this.verbClass = verbClass;
        this.taskClass = taskClass;
    }

    @Override
    public boolean matchSchema(SemanticsModel resolvedMeaning) {
        return (resolvedMeaning.newGetSlotPathFiller("verb.class").equals(verbClass.getSimpleName()) &&
                resolvedMeaning.newGetSlotPathFiller("dialogAct").equals("Command"));
    }

    @Override
    public NonDialogTask applySchema(SemanticsModel resolvedMeaning) {
        try {
            NonDialogTask task = taskClass.newInstance();
            task.setTaskSpec(SemanticsModel.parseJSON(((JSONObject) resolvedMeaning.newGetSlotPathFiller("verb")).toJSONString()));
            return task;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
