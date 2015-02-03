package edu.cmu.sv;

import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.junit.Test;


/**
 * Created by David Cohen on 10/29/14.
 */
public class TestLoadDatabase {

    @Test
    public void Test() {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        String queryString = yodaEnvironment.db.prefixes +
                "SELECT DISTINCT ?x WHERE {?x rdf:type ?ys}";

        System.out.println("Number of things in DB instances:" +
                yodaEnvironment.db.runQuerySelectX(queryString).size());


    }

}
