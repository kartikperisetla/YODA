package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * YODA's built-in NLG module
 */
public class NaturalLanguageGenerator {
    private static Logger logger = Logger.getLogger("yoda.natural_language_generation.NaturalLanguageGenerator");
    private static FileHandler fh;
    static {
        try {
            fh = new FileHandler("NaturalLanguageGenerator.log");
            fh.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        logger.addHandler(fh);
    }
    public static Random random = new Random();
    public static RandomData randomData = new RandomDataImpl();
    YodaEnvironment yodaEnvironment;
    // grammar preferences can be changed every time the NLG module is called
    public Grammar.GrammarPreferences grammarPreferences;

    public NaturalLanguageGenerator(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    public void speak(SemanticsModel model, Grammar.GrammarPreferences grammarPreferences){
        logger.info("nlg request made:"+model);
        Map.Entry<String, SemanticsModel> chosenUtterance = generateBestForSemantics(model, grammarPreferences);
        logger.info("chosen utterance:"+chosenUtterance);
        yodaEnvironment.out.sendOutput(chosenUtterance.getKey());
        Turn systemTurn = new Turn("system", chosenUtterance.getValue(), model, null, null);
        Calendar calendar = Calendar.getInstance();
        yodaEnvironment.DstInputQueue.add(new ImmutablePair<>(systemTurn, calendar.getTimeInMillis()));
    }

    /*
    * The NLG function that is called by a dialog system at each turn.
    * This function may make use of various information in the yodaEnvironment
    * to select from the many possible expressions.
    * */
    public Map.Entry<String, SemanticsModel> generateBestForSemantics(SemanticsModel model, Grammar.GrammarPreferences grammarPreferences){
        return new LinkedList<>(generateAll(model, yodaEnvironment, grammarPreferences).entrySet()).get(0);
    }

    public Map<String, SemanticsModel> generateAll(SemanticsModel model, YodaEnvironment yodaEnvironment,
                                                   Grammar.GrammarPreferences grammarPreferences){
        this.grammarPreferences = grammarPreferences;
        Map<String, SemanticsModel> ans = new HashMap<>();
        for (Class<? extends Template> templateCls : Grammar.grammar1_roots){
            try {
                templateCls.newInstance().generateAll(model.getInternalRepresentation(), yodaEnvironment,
                        this.grammarPreferences.maxUtteranceDepth).
                        entrySet().forEach(x -> ans.put(x.getKey(), new SemanticsModel(x.getValue())));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (ans.size()==0){
            ans.put("NLG currently can't generate an utterance for this meaning",
                    new SemanticsModel(model.getInternalRepresentation().toJSONString()));
        }
        return ans;
    }

    // currently, this will overwrite semantic interpretations of identical strings,
    // i.e. ambiguity is ignored at all levels of generation
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth){
        Map<String, JSONObject> ans = new HashMap<>();
        if (remainingDepth==0)
            return ans;
        for (Class<? extends Template> templateCls : Grammar.grammar1){
            try {
                templateCls.newInstance().generateAll(constraints, yodaEnvironment, remainingDepth).
                        entrySet().forEach(x -> ans.put(x.getKey(), x.getValue()));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return ans;
    }

}
