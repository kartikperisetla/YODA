package edu.cmu.sv.yoda_environment;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.dialog_management.DialogManager;
import edu.cmu.sv.dialog_state_tracking.DialogStateHypothesis;
import edu.cmu.sv.dialog_state_tracking.DialogStateTracker;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.natural_language_generation.NaturalLanguageGenerator;
import edu.cmu.sv.spoken_language_understanding.RegexUnderstander;
import edu.cmu.sv.spoken_language_understanding.SpokenLanguageUnderstander;
import edu.cmu.sv.utils.StringDistribution;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by David Cohen on 10/14/14.
 *
 * A YODA Environment contains references to all the modules and databases required for a
 * functioning dialog system.
 *
 */
public class YodaEnvironment {
    // remove console logging from the root log handler
    static{
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }
    }

    public DialogStateTracker dst;
    public DialogManager dm;
    public Database db;
    public NaturalLanguageGenerator nlg;
    public SpokenLanguageUnderstander slu;
    public OutputHandler out;

    // turn + time stamp of DST input
    public BlockingQueue<Pair<Turn, Long>> DstInputQueue = new LinkedBlockingDeque<>();
    // DM input
    public BlockingQueue<Pair<Map<String, DialogStateHypothesis>, StringDistribution>> DmInputQueue = new LinkedBlockingDeque<>();


    public static YodaEnvironment dstTestingEnvironment(){
        YodaEnvironment ans = new YodaEnvironment();
        ans.dst = new DialogStateTracker(ans);
        ans.db = new Database(ans);
        return ans;
    }

    public static YodaEnvironment dialogTestingEnvironment(){
        YodaEnvironment ans = new YodaEnvironment();
        ans.dst = new DialogStateTracker(ans);
        ans.db = new Database(ans);
        ans.dm = new DialogManager(ans);
        ans.nlg = new NaturalLanguageGenerator(ans);
        ans.slu = new RegexUnderstander(ans);
        ans.out = new StandardOutOutputHandler();
        return ans;
    }

}
