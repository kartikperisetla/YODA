package edu.cmu.sv.spoken_language_understanding.regex_plus_keyword_understander;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

/**
 * Created by David Cohen on 1/21/15.
 */
public class NamedEntityFragmentInterpreter implements MiniLanguageInterpreter {
    Class<? extends Thing> thingClass;

    public NamedEntityFragmentInterpreter(Class<? extends Thing> thingClass) {
        this.thingClass = thingClass;
    }

    @Override
    public Pair<JSONObject, Double> interpret(String utterance, YodaEnvironment yodaEnvironment) {
        String namedEntityString = utterance;
        String jsonString = "{\"dialogAct\":\"Fragment\",\"topic\":{\"HasName\":\""+namedEntityString+"\","+
                "\"class\":\""+thingClass.getSimpleName()+"\"}}";
        return new ImmutablePair<>(SemanticsModel.parseJSON(jsonString), 0.1);
    }
}
