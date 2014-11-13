package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * YODA's built-in NLG module
 */
public class NaturalLanguageGenerator {
    public static Random random = new Random();
    public static RandomData randomData = new RandomDataImpl();
    YodaEnvironment yodaEnvironment;
    // grammar preferences can be changed every time the NLG module is called
    public Grammar.GrammarPreferences grammarPreferences;

    public NaturalLanguageGenerator(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
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

    // TODO: implement
    public Map<String, SemanticsModel> generateCorpus(){
        return null;
    }

}
