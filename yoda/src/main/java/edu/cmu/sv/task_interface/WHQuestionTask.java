package edu.cmu.sv.task_interface;

/**
 * Created by David Cohen on 9/3/14.
 */
public class WHQuestionTask implements DialogTask {
    DialogTaskPreferences preferences = new DialogTaskPreferences(1,5,3);

    @Override
    public DialogTaskPreferences getPreferences() {
        return preferences;
    }
}
