package edu.cmu.sv;

import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.noun.Noun;
import edu.cmu.sv.ontology.noun.poi_types.Cafe;
import edu.cmu.sv.ontology.preposition.IsCloseTo;
import edu.cmu.sv.ontology.role.*;
import edu.cmu.sv.ontology.role.has_quality_subroles.HasDistance;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 */
public class TestResolveReferences {

    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        String empty = "{\"class\":\""+UnknownThingWithRoles.class.getSimpleName()+"\"}";

        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        String restaurantSelectionQuery = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:Restaurant . \n }";
        List<String> restaurantURIList = new LinkedList<>(yodaEnvironment.db.runQuerySelectX(restaurantSelectionQuery));

        Random r = new Random();
        for (String restaurantURI : restaurantURIList){
            // randomly insert Expensiveness
            String expensivenessInsertString = yodaEnvironment.db.prefixes +
                    "INSERT DATA {<"+restaurantURI+"> base:expensiveness "+r.nextDouble()+"}";
            try {
                Update update = yodaEnvironment.db.connection.prepareUpdate(QueryLanguage.SPARQL, expensivenessInsertString, yodaEnvironment.db.baseURI);
                update.execute();
            } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                e.printStackTrace();
            }
        }

        // simple named entity reference resolution
        String sluNamedEntityChunkURI = yodaEnvironment.db.insertValue("red rock");
        JSONObject reference = SemanticsModel.parseJSON(OntologyRegistry.webResourceWrap(sluNamedEntityChunkURI));
        SemanticsModel.wrap(reference, Noun.class.getSimpleName(), HasName.class.getSimpleName());
        System.out.println(reference.toJSONString());

        StringDistribution possibleReferences = ReferenceResolution.resolveReference(yodaEnvironment, reference, false);
        for (String possibleReference : possibleReferences.keySet()){
            String labelQuery = "SELECT ?x WHERE { <"+possibleReference+"> rdfs:label ?x}";
            System.out.println("--- Possible Referent: --- (score = "+possibleReferences.get(possibleReference)+")");
            System.out.println(yodaEnvironment.db.runQuerySelectX(labelQuery));
        }


        // description with a nested PP reference resolution
        sluNamedEntityChunkURI = yodaEnvironment.db.insertValue("tied house");
        reference = SemanticsModel.parseJSON(OntologyRegistry.webResourceWrap(sluNamedEntityChunkURI));
        SemanticsModel.wrap(reference, Noun.class.getSimpleName(), HasName.class.getSimpleName());
        SemanticsModel.wrap(reference, IsCloseTo.class.getSimpleName(), InRelationTo.class.getSimpleName());
        SemanticsModel.wrap(reference, Cafe.class.getSimpleName(), HasDistance.class.getSimpleName());
        System.out.println(reference.toJSONString());

        possibleReferences = ReferenceResolution.resolveReference(yodaEnvironment, reference, false);
        for (String possibleReference : possibleReferences.keySet()){
            String labelQuery = "SELECT ?x WHERE { <"+possibleReference+"> rdfs:label ?x}";
            System.out.println("--- Possible Referent: --- (score = "+possibleReferences.get(possibleReference)+")");
            System.out.println(yodaEnvironment.db.runQuerySelectX(labelQuery));
        }






    }

}
