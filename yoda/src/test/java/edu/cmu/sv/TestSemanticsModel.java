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

        String uri1 = null;
        String uri2 = null;
        String uri3 = null;
        String uri4 = null;
        try {
            uri1 = yodaEnvironment.db.insertValue(1);
            uri2 = yodaEnvironment.db.insertValue(10);
            uri3 = yodaEnvironment.db.insertValue(5);
            uri4 = yodaEnvironment.db.insertValue(30);
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
                "                      \"HasValues\":[\n" +
                "                                {\"class\":\"Time\",\n" +
                "                                 \"HasHour\":\""+uri1+"\"},\n" +
                "                                {\"class\":\"Time\",\n" +
                "                                 \"HasHour\":\""+uri2+"\"}\n" +
                "                               ]}}\n" +
                "}";
        SemanticsModel sm2 = new SemanticsModel(jsonString);

        System.out.println("sm2.getAllSlotFillerPairs():\n"+sm2.getSlotPathFiller("topic"));
        System.out.println("sm2.getAllSlotFillerPairs():\n"+sm2.getSlotPathFiller("topic.HasAtTime.HasHour"));
        System.out.println("sm2.getAllSlotFillerPairs():\n"+sm2.getSlotPathFiller("verb.Patient.HasAtTime"));


        System.out.println();
        System.out.println("SemanticsModel.extendAndOverwrite() demonstration:");

        jsonString = "{\"class\":\"Meeting\"}";
        SemanticsModel sm3 = new SemanticsModel(jsonString);
        jsonString = "{\"class\":\"UnknownThingWithRoles\",\n" +
                " \"HasAtTime\":{ \"class\":\"Time\",\n" +
                "               \"HasHour\":\""+uri3+"\" }\n" +
                "}";
        SemanticsModel sm4 = new SemanticsModel(jsonString);
        System.out.println(sm3);
        System.out.println(sm4);
        sm3.extendAndOverwrite(sm4);
        System.out.println(sm3);

        System.out.println();
        System.out.println("SemanticsModel.extendAndOverwriteAtPoint() demonstration:");


        jsonString = "{\n" +
                "\"dialogAct\":\"Command\",\n" +
                "\"verb\":{\"class\":\"Create\",\n" +
                "        \"Patient\":{\"class\":\"Meeting\",\n" +
                "                   \"HasAtTime\":{\"class\":\"Time\",\n" +
                "                                \"HasHour\":\""+uri1+"\"}}}\n" +
                "}";
        SemanticsModel sm5 = new SemanticsModel(jsonString);
        System.out.println(sm5);

        jsonString = "{\n" +
                "\"class\":\"UnknownThingWithRoles\",\n" +
                "\"HasTenMinute\":\""+uri4+"\"\n" +
                "}";
        SemanticsModel sm6 = new SemanticsModel(jsonString);
        System.out.println(sm6);

        sm5.extendAndOverwriteAtPoint("verb.Patient.HasAtTime", sm6);
        System.out.println(sm5);
    }
}
