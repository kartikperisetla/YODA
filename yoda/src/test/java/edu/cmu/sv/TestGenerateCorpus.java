package edu.cmu.sv;

import edu.cmu.sv.semantics.SemanticsModel;
import org.junit.Test;

/**
 * Created by David Cohen on 10/29/14.
 */
public class TestGenerateCorpus {

    @Test
    public void Test() {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        String queryString = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:PointOfInterest . \n }";

        for (String uri : yodaEnvironment.db.runQuerySelectX(queryString)) {
            SemanticsModel ex = new SemanticsModel("{\"dialogAct\": \"Fragment\", \"topic\": " +
                    TestDSTClarification.WebResourceWrap(uri) + "}");
            yodaEnvironment.nlg.generateAll(ex, yodaEnvironment).keySet().forEach(System.out::println);
        }
    }

}
