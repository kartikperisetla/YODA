package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasName;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 */
public class SimpleNamedEntityFromLabelTemplate0 implements Template {
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
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
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
            JSONObject content = SemanticsModel.parseJSON(constraints.toJSONString());
            SemanticsModel.wrap(content, yodaEnvironment.db.mostSpecificClass(entityURI),
                    HasName.class.getSimpleName());
            Map<String, JSONObject> tmp = new HashMap<>();
            tmp.put(label, content);
            Map<String, Pair<Integer, Integer>> tmp2 = new HashMap<>();
            tmp2.put(HasName.class.getSimpleName(), new ImmutablePair<>(0,0));
            ans.put(label, GenerationUtils.simpleOrderedCombinations(Arrays.asList(tmp),
                    x -> x.get(0), tmp2).get(label));
        }

        return ans;
    }
}
