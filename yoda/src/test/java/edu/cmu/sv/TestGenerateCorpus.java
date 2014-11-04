package edu.cmu.sv;

import edu.cmu.sv.ontology.OntologyRegistry;
import edu.cmu.sv.ontology.quality.Expensiveness;
import edu.cmu.sv.ontology.role.has_quality_subroles.HasAbsoluteQualityDegree;
import edu.cmu.sv.ontology.role.has_quality_subroles.HasExpensiveness;
import edu.cmu.sv.semantics.SemanticsModel;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 */
public class TestGenerateCorpus {

    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        String outputFileName = "/home/cohend/YODA_corpus.txt";
        PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");

        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();

        String poiSelectionQuery = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:PointOfInterest . \n }";
        String restaurantSelectionQuery = yodaEnvironment.db.prefixes +
                "SELECT ?x WHERE { ?x rdf:type base:Restaurant . \n }";
        List<String> poiURIList = new LinkedList<>(yodaEnvironment.db.runQuerySelectX(poiSelectionQuery));
        List<String> restaurantURIList = new LinkedList<>(yodaEnvironment.db.runQuerySelectX(restaurantSelectionQuery));

        // randomly insert the IsCloseTo relation between two POIs
        for (int i = 0; i < 100; i++) {
            String isCloseToInsertString = yodaEnvironment.db.prefixes +
                    "INSERT DATA \n{<"+poiURIList.get(2*i)+"> base:IsCloseTo <"+poiURIList.get(2*i+1)+">}";
            yodaEnvironment.db.insertStatement(isCloseToInsertString);
        }

        Random r = new Random();
        for (String restaurantURI : restaurantURIList){
            // randomly insert Expensiveness
            yodaEnvironment.db.assignQuantityToEntityQuality(restaurantURI, HasExpensiveness.class, Expensiveness.class, r.nextDouble());
        }




        Map<String, SemanticsModel> corpus = new HashMap<>();

        for (String uri : yodaEnvironment.db.runQuerySelectX(poiSelectionQuery)) {
            SemanticsModel ex = new SemanticsModel("{\"dialogAct\": \"Fragment\", \"topic\": " +
                    OntologyRegistry.WebResourceWrap(uri) + "}");
            Map<String, SemanticsModel> tmp = yodaEnvironment.nlg.generateAll(ex, yodaEnvironment);
            for (String key : tmp.keySet()){
                corpus.put(key, tmp.get(key));
            }

            // usually, the command won't have a topic,
            // but this is a quick way to generate a more interesting corpus for Bing
            SemanticsModel ex2 = new SemanticsModel("{\"dialogAct\": \"Command\", \"topic\": " +
                    OntologyRegistry.WebResourceWrap(uri) + "}");
            Map<String, SemanticsModel> tmp2 = yodaEnvironment.nlg.generateAll(ex2, yodaEnvironment);
            for (String key : tmp2.keySet()){
                corpus.put(key, tmp2.get(key));
            }

        }

        for (String key : corpus.keySet()){
//            System.out.println(key);
//            System.out.println(corpus.get(key));
            writer.write("---\n");
            writer.write(key+"\n");
            writer.write(corpus.get(key).getInternalRepresentation().toJSONString()+"\n");
        }
        writer.close();

    }

}
