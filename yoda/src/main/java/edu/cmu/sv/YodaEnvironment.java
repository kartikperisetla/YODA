package edu.cmu.sv;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_management.DialogManager;
import edu.cmu.sv.dialog_state_tracking.DialogStateTracker2;

/**
 * Created by David Cohen on 10/14/14.
 *
 * A YODA Environment contains references to all the modules and databases required for a
 * functioning dialog system.
 *
 */
public class YodaEnvironment {
    public DialogStateTracker2 dst;
    public DialogManager dm;
    public Database db;
    // public SpokenLanguageUnderstander slu;
    // public NaturalLanguageGenerator nlg;


    public static YodaEnvironment dstTestingEnvironment(){
        YodaEnvironment ans = new YodaEnvironment();
        ans.dst = new DialogStateTracker2(ans);
        ans.db = new Database();
        return ans;
    }

    public static YodaEnvironment dialogTestingEnvironment(){
        YodaEnvironment ans = new YodaEnvironment();
        ans.dst = new DialogStateTracker2(ans);
        ans.db = new Database();
        ans.dm = new DialogManager(ans);
        return ans;
    }
}
