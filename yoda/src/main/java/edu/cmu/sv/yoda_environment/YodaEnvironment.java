package edu.cmu.sv.yoda_environment;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_management.DialogManager;
import edu.cmu.sv.dialog_state_tracking.DialogStateTracker2;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.spoken_language_understanding.KeywordUnderstander;
import edu.cmu.sv.spoken_language_understanding.SpokenLanguageUnderstander;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

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
    public SpokenLanguageUnderstander slu;

    // turn + time stamp of DST input
    public BlockingQueue<Pair<Turn, Long>> DstInputQueue = new LinkedBlockingDeque<>();
    // DM input
    public BlockingQueue<DiscourseUnit2> DmInputQueue = new LinkedBlockingDeque<>();


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
        ans.slu = new KeywordUnderstander(ans);
        return ans;
    }

    /*
    * Environment for running actual mixed-initiative dialog systems
    * */
    public static YodaEnvironment dialogSystemEnvironment(){
        YodaEnvironment ans = new YodaEnvironment();
        ans.dst = new DialogStateTracker2(ans);
        ans.db = new Database(ans);
        ans.dm = new DialogManager(ans);
        ans.nlg = new NaturalLanguageGenerator(ans);
        ans.slu = new KeywordUnderstander(ans);

        Thread dstThread = new Thread(ans.dst);
        dstThread.start();
        Thread dmThread = new Thread(ans.dm);
        dmThread.start();

        return ans;
    }




}
