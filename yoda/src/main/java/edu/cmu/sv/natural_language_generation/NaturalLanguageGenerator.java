package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * YODA's built-in NLG module
 */
public class NaturalLanguageGenerator {
    YodaEnvironment yodaEnvironment;

    public NaturalLanguageGenerator(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }

    /*
    * The NLG function that is called by a dialog system at each turn.
    * This function may make use of various information in the yodaEnvironment
    * to select from the many possible expressions.
    * */
    public Map.Entry<String, SemanticsModel> generateBestForSemantics(SemanticsModel model){
        return new LinkedList<>(generateAll(model, yodaEnvironment).entrySet()).get(0);
    }

    public Map<String, SemanticsModel> generateAll(SemanticsModel model, YodaEnvironment yodaEnvironment){

        Map<String, SemanticsModel> ans = new HashMap<>();
        for (Class<? extends Template> templateCls : GrammarRegistry.grammar1_roots){
            try {
                templateCls.newInstance().generateAll(model.getInternalRepresentation(), yodaEnvironment).
                        entrySet().forEach(x -> ans.put(x.getKey(), new SemanticsModel(x.getValue())));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return ans;

    }

    // currently, this will overwrite semantic interpretations of identical strings,
    // i.e. ambiguity is ignored at all levels of generation
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment){
        Map<String, JSONObject> ans = new HashMap<>();
        for (Class<? extends Template> templateCls : GrammarRegistry.grammar1){
            try {
                templateCls.newInstance().generateAll(constraints, yodaEnvironment).
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
