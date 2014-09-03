package edu.cmu.sv.task_interface;

/**
 * Created by David Cohen on 9/3/14.
 *
 * This task answers a yes/no question by performing appropriate database lookups
 */
public class YNQuestionTask implements DialogTask{
    DialogTaskPreferences preferences = new DialogTaskPreferences(1.0, 5.0, 3.0);

    @Override
    public DialogTaskPreferences getPreferences() {
        return preferences;
    }
}
