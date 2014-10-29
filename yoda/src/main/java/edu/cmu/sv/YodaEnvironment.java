package edu.cmu.sv;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_management.DialogManager;
import edu.cmu.sv.dialog_state_tracking.DialogStateTracker2;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;

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
    public NaturalLanguageGenerator nlg;
    // public SpokenLanguageUnderstander slu;


    public static YodaEnvironment dstTestingEnvironment(){
        YodaEnvironment ans = new YodaEnvironment();
        ans.dst = new DialogStateTracker2(ans);
        ans.db = new Database(ans);
        return ans;
    }

    public static YodaEnvironment dialogTestingEnvironment(){
        YodaEnvironment ans = new YodaEnvironment();
        ans.dst = new DialogStateTracker2(ans);
        ans.db = new Database(ans);
        ans.dm = new DialogManager(ans);
        ans.nlg = new NaturalLanguageGenerator(ans);
        return ans;
    }
}
