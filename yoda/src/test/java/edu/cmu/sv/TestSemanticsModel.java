package edu.cmu.sv;

import edu.cmu.sv.semantics.SemanticsModel;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;


/**
 * Created by David Cohen on 10/15/14.
 */
public class TestSemanticsModel {

    @Test
    public void Test() throws ParseException {

        String jsonString;
        YodaEnvironment yodaEnvironment;
        yodaEnvironment = YodaEnvironment.dstTestingEnvironment();

        /// Turn 1
        String uri1 = null;
        String uri2 = null;
        try {
            uri1 = yodaEnvironment.db.insertValue(1);
            uri2 = yodaEnvironment.db.insertValue(10);
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
            e.printStackTrace();
        }

        jsonString = "{\n" +
                "\"dialogAct\":\"Command\",\n" +
                "\"verb\":{\"class\":\"Create\",\n" +
                "        \"Patient\":{\"class\":\"Meeting\",\n" +
                "                   \"HasAtTime\":{\"class\":\"Time\",\n" +
                "                                \"HasHour\":\""+uri1+"\"}}}\n" +
                "}";
        SemanticsModel sm1 = new SemanticsModel(jsonString);

        System.out.println("sm1.getAllSlotFillerPairs():\n"+sm1.getSlotPathFiller("verb.Patient.HasAtTime"));

        jsonString = "{\n" +
                "\"dialogAct\":\"RequestDisambiguateValues\",\n" +
                "\"topic\":{\"class\":\"UnknownThingWithRoles\",\n" +
                "         \"HasAtTime\":{\"class\": \"Or\",\n" +
                "                      \"Values\":[\n" +
                "                                {\"class\":\"Time\",\n" +
                "                                 \"HasHour\":\""+uri1+"\"},\n" +
                "                                {\"class\":\"Time\",\n" +
                "                                 \"HasHour\":\""+uri2+"\"}\n" +
                "                               ]}}\n" +
                "}";
        SemanticsModel sm2 = new SemanticsModel(jsonString);

        System.out.println("sm2.getAllSlotFillerPairs():\n"+sm2.getSlotPathFiller("topic"));
        System.out.println("sm2.getAllSlotFillerPairs():\n"+sm2.getSlotPathFiller("topic.HasAtTime.HasHour"));
//        System.out.println("sm2.getAllSlotFillerPairs():\n"+sm2.getSlotPathFiller("verb.Patient.HasAtTime"));


    }


}
