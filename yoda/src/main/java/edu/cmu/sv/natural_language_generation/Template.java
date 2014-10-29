package edu.cmu.sv.natural_language_generation;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/27/14.
 *
 * A template corresponds to a CFG rule for generation
 *
 */
public interface Template {
    /*
    * Constraints are passed from above,
    * and the template generates strings /
    * elaborated constraints to pass back up to the parent template
    * */
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment);

}
