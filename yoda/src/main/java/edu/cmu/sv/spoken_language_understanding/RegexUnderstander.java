package edu.cmu.sv.spoken_language_understanding;

import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 11/21/14.
 *
 * A simple keyword-based SLU system for quick-n-dirty tests
 *
 */
public class RegexUnderstander implements SpokenLanguageUnderstander{
    private static Logger logger = Logger.getLogger("yoda.spoken_language_understanding.RegexUnderstander");
    private static FileHandler fh;
    static {
        try {
            fh = new FileHandler("RegexUnderstander.log");
            fh.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        logger.addHandler(fh);
    }


    @Override
    public void process1BestAsr(String asrResult) {
        logger.info("input asr result:"+asrResult);
        if (asrResult.length()==0)
            return;

        Map<String, SemanticsModel> hypotheses = new HashMap<>();
        StringDistribution hypothesisDistribution = new StringDistribution();
        int hypothesisId = 0;

        String jsonString = "{}";

        Pattern howExpensivePattern = Pattern.compile("how (cheap|expensive)( is| are| )(.+)");
        Matcher m = howExpensivePattern.matcher(asrResult);
        if (m.matches()) {
            String PoiName = m.group(2);
            System.out.println(PoiName);
            logger.info("chunked POI: "+ PoiName);
            String uri = yodaEnvironment.db.insertValue(PoiName);
            jsonString = "{\"dialogAct\":\"WHQuestion\",\"verb\":{\"Agent\":{\"HasName\":{\"HasURI\":\""+uri+"\",\"class\":\"WebResource\"},\"class\":\"PointOfInterest\"},\"Patient\":{\"class\":\"UnknownThingWithRoles\",\"HasExpensiveness\":{\"class\":\"Expensive\"}},\"class\":\"HasProperty\"}}";
            SemanticsModel interpretation = new SemanticsModel(jsonString);
            hypotheses.put("hyp"+hypothesisId, interpretation);
            hypothesisDistribution.put("hyp"+hypothesisId, 1.0);
            hypothesisId++;
        }

        Pattern isExpensivePattern = Pattern.compile("(is |are |)(the |)?(.+)(expensive)");
        Matcher m2 = isExpensivePattern.matcher(asrResult);
        if (m2.matches()) {
            String PoiName = m2.group(3);
            logger.info("chunked POI: "+ PoiName);
            String uri = yodaEnvironment.db.insertValue(PoiName);
            jsonString = "{\"dialogAct\":\"YNQuestion\",\"verb\":{\"Agent\":{\"HasName\":{\"HasURI\":\""+uri+"\",\"class\":\"WebResource\"},\"class\":\"PointOfInterest\"},\"Patient\":{\"class\":\"UnknownThingWithRoles\",\"HasExpensiveness\":{\"class\":\"Expensive\"}},\"class\":\"HasProperty\"}}";
            SemanticsModel interpretation = new SemanticsModel(jsonString);
            hypotheses.put("hyp"+hypothesisId, interpretation);
            hypothesisDistribution.put("hyp"+hypothesisId, 1.0);
            hypothesisId++;
        }

        Pattern acceptPattern = Pattern.compile("yes");
        Matcher m3 = acceptPattern.matcher(asrResult);
        if (m3.matches()) {
            jsonString = "{\"dialogAct\":\"Accept\"}";
            SemanticsModel interpretation = new SemanticsModel(jsonString);
            hypotheses.put("hyp"+hypothesisId, interpretation);
            hypothesisDistribution.put("hyp"+hypothesisId, 1.0);
            hypothesisId++;
        }

        Pattern rejectPattern = Pattern.compile("no");
        Matcher m4 = rejectPattern.matcher(asrResult);
        if (m4.matches()) {
            jsonString = "{\"dialogAct\":\"Reject\"}";
            SemanticsModel interpretation = new SemanticsModel(jsonString);
            hypotheses.put("hyp"+hypothesisId, interpretation);
            hypothesisDistribution.put("hyp"+hypothesisId, 1.0);
            hypothesisId++;
        }

        Pattern fragmentNamedEntityPattern = Pattern.compile("(.+)");
        Matcher m5 = fragmentNamedEntityPattern.matcher(asrResult);
        if (m5.matches()) {
            String PoiName = m5.group(0);
            String uri = yodaEnvironment.db.insertValue(PoiName);
            jsonString = "{\"dialogAct\":\"Fragment\", \"topic\":{\"HasName\":{\"HasURI\":\""+uri+"\",\"class\":\"WebResource\"},\"class\":\"PointOfInterest\"}}";
            SemanticsModel interpretation = new SemanticsModel(jsonString);
            hypotheses.put("hyp"+hypothesisId, interpretation);
            hypothesisDistribution.put("hyp"+hypothesisId, 0.1);
            hypothesisId++;
        }


        // create a turn and update the DST
        hypothesisDistribution.normalize();
        Turn newTurn = new Turn("user", null, null, hypotheses, hypothesisDistribution);
        Calendar calendar = Calendar.getInstance();
        yodaEnvironment.DstInputQueue.add(new ImmutablePair<>(newTurn, calendar.getTimeInMillis()));
        logger.info("hypothesis distribution:" + hypothesisDistribution);
        logger.info("hypotheses:"+hypotheses);
    }

    @Override
    public void processNBestAsr(StringDistribution asrNBestResult) {
        process1BestAsr(asrNBestResult.getTopHypothesis());
    }

    YodaEnvironment yodaEnvironment;
    public RegexUnderstander(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }
}
