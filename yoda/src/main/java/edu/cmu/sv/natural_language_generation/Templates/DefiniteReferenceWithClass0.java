package edu.cmu.sv.natural_language_generation.Templates;

import edu.cmu.sv.YodaEnvironment;
import edu.cmu.sv.database.Database;
import edu.cmu.sv.natural_language_generation.LexicalEntry;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.Thing;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 10/30/14.
 */
public class DefiniteReferenceWithClass0 implements Template {
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
                "SELECT ?x WHERE { <"+entityURI+"> rdf:type ?x .}";

        Set<String> classNames = yodaEnvironment.db.runQuerySelectX(queryString);
//        System.out.println("Class name(s) from database:" + classNames);

        try {
            for (String clsName : classNames.stream().map(Database::getLocalName).
                    collect(Collectors.toList())) {
                if (!OntologyRegistry.thingNameMap.containsKey(clsName))
                    continue;
                Class<? extends Thing> cls = OntologyRegistry.thingNameMap.get(clsName);
                if (Modifier.isAbstract(cls.getModifiers()))
                    continue;
                for (LexicalEntry lexicalEntry : cls.newInstance().getLexicalEntries()) {
                    for (String singularNounForm : lexicalEntry.singularNounForms){
                        ans.put("the "+singularNounForm,
                                (JSONObject)SemanticsModel.parser.
                                        parse("{\"class\":\"" + clsName + "\"}"));
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | ParseException e) {
            e.printStackTrace();
        }
        return ans;
    }
}
