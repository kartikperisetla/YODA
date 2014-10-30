package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by David Cohen on 10/29/14.
 */
public class NamedEntityFromLabelTemplate0 implements Template {
    static JSONObject applicabilityConstraint;
    static {
        try {
            applicabilityConstraint= (JSONObject)SemanticsModel.parser.
                    parse("{\"class\":\"" + WebResource.class.getSimpleName() + "\"}");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment) {
        Map<String, JSONObject> ans = new HashMap<>();
        if (SemanticsModel.anySenseConflicts(applicabilityConstraint, constraints))
            return ans;
        String entityURI = (String) new SemanticsModel(constraints).
                newGetSlotPathFiller(HasURI.class.getSimpleName());

        String queryString = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { <"+entityURI+"> rdfs:label ?x .}";

        Set<String> labels = yodaEnvironment.db.runQuerySelectX(queryString);
//        System.out.println("Label(s) from database:" + labels);

        for (String label : labels){
            try {
                ans.put(label, (JSONObject) SemanticsModel.parser.parse(constraints.toJSONString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return ans;
    }
}
