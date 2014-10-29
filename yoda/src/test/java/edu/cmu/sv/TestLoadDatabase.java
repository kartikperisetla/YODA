package edu.cmu.sv;

import org.junit.Test;


/**
 * Created by David Cohen on 10/29/14.
 */
public class TestLoadDatabase {

    @Test
    public void Test() {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();


        String queryString = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdfs:subClassOf base:PointOfInterest . \n }";

        System.out.println("Number of classes that inherit from POI:" +
                yodaEnvironment.db.runQuerySelectX(queryString).size());

        queryString = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:PointOfInterest . \n }";

        System.out.println("Number of POI instances:" +
                yodaEnvironment.db.runQuerySelectX(queryString).size());


    }

}
