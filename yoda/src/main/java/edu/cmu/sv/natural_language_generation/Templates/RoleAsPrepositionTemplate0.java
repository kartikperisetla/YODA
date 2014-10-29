package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.natural_language_generation.Template;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Created by David Cohen on 10/27/14.
 *
 * Def Ref := the + cls + role-as-preposition + NP
 *
 * {cls: cls,
 * role: {NP}
 * ref-type: def}
 *
 * Examples:
 * the meeting at Red Rock
 *
 */
public class RoleAsPrepositionTemplate0 implements Template{
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        return null;
    }
}
