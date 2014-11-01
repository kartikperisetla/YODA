package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.database.Database;
import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.natural_language_generation.TemplateCombiner;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.quality.Quality;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.ontology.role.Role;
import edu.cmu.sv.semantics.SemanticsModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 10/31/14.
 */
public class QualityAdjectiveTemplate0 implements Template {

    static JSONObject applicabilityConstraint;

    static {
        try {
            applicabilityConstraint = (JSONObject) SemanticsModel.parser.
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
                "SELECT ?x ?y WHERE { ?y rdf:type base:" + Quality.class.getSimpleName() + " . \n" +
                "<" + entityURI + "> ?x ?y . \n" +
                "?x rdfs:subPropertyOf base:" + Role.class.getSimpleName() + " .}";
        Set<Pair<String, String>> roleFillerPairs = yodaEnvironment.db.runQuerySelectXAndY(queryString2);

        if (roleFillerPairs.size()>0)
            System.out.println("roles and qualities:" + roleFillerPairs);

        Map<String, JSONObject> clsChunks = new HashMap<>();


        try {
            // collect class name chunks
            for (String clsName : classNames.stream().map(Database::getLocalName).
                    collect(Collectors.toList())) {
                if (!OntologyRegistry.thingNameMap.containsKey(clsName))
                    continue;
                Class<? extends Thing> cls = OntologyRegistry.thingNameMap.get(clsName);
                if (Modifier.isAbstract(cls.getModifiers()))
                    continue;

                for (LexicalEntry lexicalEntry : cls.newInstance().getLexicalEntries()) {
                    for (String singularNounForm : lexicalEntry.singularNounForms) {
                        clsChunks.put(singularNounForm, SemanticsModel.parseJSON("{\"class\":\"" + clsName + "\"}"));
                    }
                }
            }

            // collect role / filler chunks
            for (Pair<String, String> roleFillerPair : roleFillerPairs) {
                Map<String, JSONObject> ppChunks = new HashMap<>();
                Map<String, JSONObject> childChunks = new HashMap<>();

                String roleName = Database.getLocalName(roleFillerPair.getKey());

                if (!OntologyRegistry.thingNameMap.containsKey(roleName))
                    continue;
                Class<? extends Thing> cls = OntologyRegistry.thingNameMap.get(roleName);
                if (Modifier.isAbstract(cls.getModifiers()))
                    continue;

                for (LexicalEntry lexicalEntry : cls.newInstance().getLexicalEntries()) {
                    for (String ppForms : lexicalEntry.relationalPrepositionalPhrases) {
                        ppChunks.put(ppForms,
                                SemanticsModel.parseJSON("{\"HasRole\":\"" + roleName + "\"}"));
                    }
                }

                JSONObject childContent = SemanticsModel.parseJSON(
                        OntologyRegistry.WebResourceWrap(roleFillerPair.getValue()));
                yodaEnvironment.nlg.generateAll(childContent, yodaEnvironment).
                        entrySet().forEach(y -> childChunks.put(y.getKey(), y.getValue()));

                List<Map<String, JSONObject>> chunks = Arrays.asList(clsChunks, ppChunks, childChunks);
                Map<String, Pair<Integer, Integer>> childNodeChunks = new HashMap<>();
                childNodeChunks.put(roleName, new ImmutablePair<>(3, 3));
                ans = TemplateCombiner.simpleOrderedCombinations(chunks,
                        QualityAdjectiveTemplate0::compositionFunction, childNodeChunks);
            }

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ans;
    }

    private static JSONObject compositionFunction(List<JSONObject> children) {
        return null;
    }
}