package edu.cmu.sv;

import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.role.IsCloseTo;
import edu.cmu.sv.semantics.SemanticsModel;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by David Cohen on 10/29/14.
 */
public class TestGenerateCorpus {

    @Test
    public void Test() {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        String queryString = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:PointOfInterest . \n }";

        // randomly insert the IsCloseTo relation between two POIs
        List<String> poiURIList = new LinkedList<>(yodaEnvironment.db.runQuerySelectX(queryString));

        String insertString = yodaEnvironment.db.prefixes +
                "INSERT DATA \n{<"+poiURIList.get(0)+"> base:IsCloseTo <"+poiURIList.get(1)+">}";
        yodaEnvironment.db.insertStatement(insertString);


        Map<String, SemanticsModel> corpus = new HashMap<>();

        for (String uri : yodaEnvironment.db.runQuerySelectX(queryString)) {
            SemanticsModel ex = new SemanticsModel("{\"dialogAct\": \"Fragment\", \"topic\": " +
                    OntologyRegistry.WebResourceWrap(uri) + "}");
            Map<String, SemanticsModel> tmp = yodaEnvironment.nlg.generateAll(ex, yodaEnvironment);
            for (String key : tmp.keySet()){
                corpus.put(key, tmp.get(key));
            }
        }

        for (String key : corpus.keySet()){
            System.out.println(key);
            System.out.println(corpus.get(key));
        }


    }

}
