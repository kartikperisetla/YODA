package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasName;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 */
public class SimpleNamedEntity0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        String entityURI;
        // ensure that the constraints match this template
        try {
            assert constraints.get("class").equals(WebResource.class.getSimpleName());
            assert constraints.keySet().size()==2;
            assert constraints.containsKey(HasURI.class.getSimpleName());
            entityURI = (String) new SemanticsModel(constraints).
                    newGetSlotPathFiller(HasURI.class.getSimpleName());
        } catch (AssertionError e){
            return new HashMap<>();
        }

        Map<String, JSONObject> ans = new HashMap<>();
        String queryString = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { <"+entityURI+"> rdfs:label ?x .}";

        Set<String> labels = yodaEnvironment.db.runQuerySelectX(queryString);
//        System.out.println("Label(s) from database:" + labels);

        for (String label : labels){
            JSONObject content = SemanticsModel.parseJSON(constraints.toJSONString());
            SemanticsModel.wrap(content, yodaEnvironment.db.mostSpecificClass(entityURI),
                    HasName.class.getSimpleName());
            Map<String, JSONObject> nameChunk = new HashMap<>();
            nameChunk.put(label, content);
            Map<String, Pair<Integer, Integer>> tmp2 = new HashMap<>();
            tmp2.put(HasName.class.getSimpleName(), new ImmutablePair<>(0,0));
            GenerationUtils.simpleOrderedCombinations(Arrays.asList(nameChunk),
                    x -> x.get(0), tmp2, yodaEnvironment).entrySet().forEach(x -> ans.put(x.getKey(), x.getValue()));
        }
        return ans;
    }
}
