package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.database.Database;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.natural_language_generation.GenerationUtils;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 10/30/14.
 */
public class DefiniteReferenceWithClassAndRelation0 implements Template {

    static JSONObject applicabilityConstraint;
    static {
        try {
            applicabilityConstraint= (JSONObject) SemanticsModel.parser.
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
                "SELECT ?x WHERE { <" + entityURI + "> rdf:type ?x .}";
        Set<String> classNames = yodaEnvironment.db.runQuerySelectX(queryString);

        String queryString2 = yodaEnvironment.db.prefixes +
                "SELECT ?x ?y WHERE { <" + entityURI + "> ?x ?y . \n" +
                "?x rdfs:subPropertyOf base:" + Role.class.getSimpleName() + " .}";
        Set<Pair<String, String>> roleFillerPairs = yodaEnvironment.db.runQuerySelectXAndY(queryString2);


        // define the chunks that this template composes
        Map<String, JSONObject> detChunks = new HashMap<>();
        Map<String, JSONObject> clsChunks = new HashMap<>();

        // compose det chunks
        detChunks.put("the", new SemanticsModel("{}").getInternalRepresentation());

        // collect class name chunks
        for (String clsName : classNames.stream().map(Database::getLocalName).
                collect(Collectors.toList())) {
            if (!OntologyRegistry.thingNameMap.containsKey(clsName))
                continue;
            Set<String> singularNounForms = GenerationUtils.getPOSForClass(
                    OntologyRegistry.thingNameMap.get(clsName), "singularNounForms");
            for (String singularNounForm : singularNounForms) {
                clsChunks.put(singularNounForm, SemanticsModel.parseJSON("{\"class\":\"" + clsName + "\"}"));
            }
        }

        // collect role / filler chunks
        for (Pair<String, String> roleFillerPair : roleFillerPairs) {
            Map<String, JSONObject> ppChunks = new HashMap<>();
            Map<String, JSONObject> childChunks = new HashMap<>();

            String roleName = Database.getLocalName(roleFillerPair.getKey());
            if (!OntologyRegistry.thingNameMap.containsKey(roleName))
                continue;
            Set<String> prepositionalPhraseForms = GenerationUtils.getPOSForClass(
                    OntologyRegistry.thingNameMap.get(roleName), "relationalPrepositionalPhrases");
            for (String ppForms : prepositionalPhraseForms) {
                ppChunks.put(ppForms,
                        SemanticsModel.parseJSON("{\"HasRole\":\"" + roleName + "\"}"));
            }

            JSONObject childContent = SemanticsModel.parseJSON(
                    OntologyRegistry.WebResourceWrap(roleFillerPair.getValue()));
            yodaEnvironment.nlg.generateAll(childContent, yodaEnvironment).
                    entrySet().forEach(y -> childChunks.put(y.getKey(), y.getValue()));

            List<Map<String, JSONObject>> chunks = Arrays.asList(detChunks, clsChunks, ppChunks, childChunks);
            Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
            childNodeChunks.put(roleName, new ImmutablePair<>(3, 3));
            ans = GenerationUtils.simpleOrderedCombinations(chunks,
                    DefiniteReferenceWithClassAndRelation0::compositionFunction, childNodeChunks);
        }

        return ans;
    }

    private static JSONObject compositionFunction(List<JSONObject> children){
        JSONObject det = children.get(0);
        JSONObject cls = children.get(1);
        JSONObject pp = children.get(2);
        JSONObject child = children.get(3);
        SemanticsModel ans = new SemanticsModel(cls.toJSONString());
        ans.extendAndOverwrite(new SemanticsModel(det.toJSONString()));
        String roleString = (String) new SemanticsModel(pp).newGetSlotPathFiller("HasRole");
        ans.extendAndOverwrite(new SemanticsModel("{\""+roleString+"\":"+child.toJSONString()+"}"));
        return ans.getInternalRepresentation();
    }

}
