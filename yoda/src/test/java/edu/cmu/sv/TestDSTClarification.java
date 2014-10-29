package edu.cmu.sv;

import edu.cmu.sv.dialog_state_tracking.DSTTester;
import edu.cmu.sv.dialog_state_tracking.DiscourseUnit2;
import edu.cmu.sv.dialog_state_tracking.Turn;
import edu.cmu.sv.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.ontology.misc.WebResource;
import edu.cmu.sv.ontology.object.Time;
import edu.cmu.sv.ontology.role.HasURI;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

import java.util.*;

/**
 * Created by David Cohen on 9/17/14.
 *
 * Run test cases to demonstrate basic clarification dialog state tracking behavior.
 *
 */
public class TestDSTClarification {


    public static String WebResourceWrap(String URI){
        String ans = "{\"class\": \""+ WebResource.class.getSimpleName()+"\", \""+
                HasURI.class.getSimpleName()+"\":\""+URI+"\"}";
        return ans;
    }

    @Test
    public void Test() {
        // Each test case is fairly complicated,
        // so to reduce the chance of making variable scope mistakes,
        // I made a separate function for each one
        try {
            for (DSTTester testDialog : Arrays.asList(testCase1())) {
                System.out.println(testDialog.evaluate());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /* 
    Test case 1: basic clarification act

        U: ambiguous between two values:
        Set up a meeting at 1
        Set up a meeting at 10
        S: requestDisambiguateValues
        1 or 10?
        U: fragment
        10
        S: Accept
        Ok
    */
    DSTTester testCase1() throws ParseException {
        String jsonString;
        YodaEnvironment yodaEnvironment;
        DSTTester testCase;
        Turn currentTurn;
        DiscourseUnit2.DialogStateHypothesis correctState;
        StringDistribution sluDistribution;
        Map<String, SemanticsModel> sluHypotheses;

        yodaEnvironment = YodaEnvironment.dstTestingEnvironment();
        testCase = new DSTTester(yodaEnvironment);

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
                "                                \"HasHour\":"+WebResourceWrap(uri1)+"}}}\n" +
                "}";
        SemanticsModel sm1 = new SemanticsModel(jsonString);

        jsonString = "{\n" +
                "\"dialogAct\":\"Command\",\n" +
                "\"verb\":{\"class\":\"Create\",\n" +
                "        \"Patient\":{\"class\":\"Meeting\",\n" +
                "                   \"HasAtTime\":{\"class\":\"Time\",\n" +
                "                                \"HasHour\":"+WebResourceWrap(uri2)+"}}}\n" +
                "}";
        SemanticsModel sm2 = new SemanticsModel(jsonString);

        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm1);
        sluHypotheses.put("hyp2", sm2);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", .6);
        sluDistribution.put("hyp2", .4);

        currentTurn = new Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByThem(sm1.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 0.0);
        testCase.getEvaluationStates().put(correctState, (float) 0.0);


        /// Turn 2
        String uri3 = null;
        try {
            uri3 = yodaEnvironment.db.insertValue(1);
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
            e.printStackTrace();
        }

        // Option 0: Confirmation request fragment: "3 o'clock?"
        jsonString = "{\n" +
                "\"dialogAct\":\"RequestConfirmValue\",\n" +
                "\"topic\":{\"class\":\""+Time.class.getSimpleName()+"\",\n" +
                "         \"HasHour\":"+WebResourceWrap(uri3)+"}\n" +
                "}\n";

        // Option 1: Confirmation request fragment: "At 3?"
//        jsonString = "{\n" +
//                "\"dialogAct\":\"RequestConfirmValue\",\n" +
//                "\"topic\":{\"class\":\"UnknownThingWithRoles\",\n" +
//                "         \"HasAtTime\":{\"class\":\"Time\",\n" +
//                "                      \"HasHour\":"+WebResourceWrap(uri3)+"}}\n" +
//                "}\n";
        SemanticsModel sm3 = new SemanticsModel(jsonString);

        currentTurn = new Turn("system", sm3.deepCopy(), null, null);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByThem(sm1.deepCopy());
        correctState.setSpokenByMe(sm3.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 1.0);
        testCase.getEvaluationStates().put(correctState, (float) 1.0);

        /// Turn 3
        String uri5 = null;
        try {
            uri5 = yodaEnvironment.db.insertValue(10);
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
            e.printStackTrace();
        }

        // Option 0, a confirmation fragment: "At 3"
//        jsonString = "{\"dialogAct\":\"Fragment\",\n" +
//                " \"topic\":{\"class\":\""+UnknownThingWithRoles.class.getSimpleName()+"\",\n" +
//                "          \"HasAtTime\":{\"class\":\"Time\",\n" +
//                "                       \"HasHour\":"+WebResourceWrap(uri5)+"}}}";

        // Option 1, a confirmation fragment "3 o'clock"
        jsonString = "{\n" +
                "\"dialogAct\":\"Fragment\",\n" +
                "\"topic\":{\"class\":\""+Time.class.getSimpleName()+"\",\n" +
                "         \"HasHour\":"+WebResourceWrap(uri5)+"}\n" +
                "}\n";

        // Option 3, an acknowledgement
//        jsonString = "{\"dialogAct\":\"Acknowledge\"}";
        SemanticsModel sm4 = new SemanticsModel(jsonString);

        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm4);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", 1.0);

        currentTurn = new Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByThem(sm2.deepCopy());
        correctState.setSpokenByMe(sm3.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 2.0);
        testCase.getEvaluationStates().put(correctState, (float) 2.0);

        return testCase;
    }

    /*
    2) deal with user non-understanding

    S: statement
    you have a meeting at 2
    U: unack
    what?
    S: repeat
    you have a meeting at 2
    */
    DSTTester testCase2() throws ParseException {
        YodaEnvironment yodaEnvironment;
        DSTTester testCase;
        Turn currentTurn;
        DiscourseUnit2.DialogStateHypothesis correctState;
        String jsonString;
        StringDistribution sluDistribution;
        Map<String, SemanticsModel> sluHypotheses;

        yodaEnvironment = YodaEnvironment.dstTestingEnvironment();
        testCase = new DSTTester(yodaEnvironment);

        /// Turn 1
        String uri1 = null;
        try {
            uri1 = yodaEnvironment.db.insertValue(2);
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
            e.printStackTrace();
        }

        jsonString = "{\n" +
                "\"dialogAct\":\"Statement\",\n" +
                "\"verb\":{\"class\":\"Exist\",\n" +
                "        \"Patient\":{\"class\":\"Meeting\",\n" +
                "                   \"HasAtTime\":{\"class\":\"Time\",\n" +
                "                                \"HasHour\":\""+WebResourceWrap(uri1)+"\"}}}\n" +
                "}";
        SemanticsModel sm1 = new SemanticsModel(jsonString);

        currentTurn = new Turn("system", sm1.deepCopy(), null, null);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByMe(sm1.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 0.0);
        testCase.getEvaluationStates().put(correctState, (float) 0.0);

        /// Turn 2
        jsonString = "{\"dialogAct\":\"NonHearing\"}";
        SemanticsModel sm2 = new SemanticsModel(jsonString);

        jsonString = "{\"dialogAct\":\"NonUnderstanding\"}";
        SemanticsModel sm3 = new SemanticsModel(jsonString);

        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm2);
        sluHypotheses.put("hyp2", sm3);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", .6);
        sluDistribution.put("hyp2", .4);

        currentTurn = new Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByMe(sm1.deepCopy());
        correctState.setSpokenByThem(sm2.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 1.0);
        testCase.getEvaluationStates().put(correctState, (float) 1.0);

        /// Turn 3
        // (the system repeats itself)
        String uri2 = null;
        try {
            uri2 = yodaEnvironment.db.insertValue(2);
        } catch (MalformedQueryException | RepositoryException | UpdateExecutionException e) {
            e.printStackTrace();
        }

        jsonString = "{\n" +
                "\"dialogAct\":\"Statement\",\n" +
                "\"verb\":{\"class\":\"Exist\",\n" +
                "        \"Patient\":{\"class\":\"Meeting\",\n" +
                "                   \"HasAtTime\":{\"class\":\"Time\",\n" +
                "                                \"HasHour\":\""+WebResourceWrap(uri2)+"\"}}}\n" +
                "}";
        SemanticsModel sm4 = new SemanticsModel(jsonString);

        currentTurn = new Turn("system", sm4.deepCopy(), null, null);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByMe(sm4.deepCopy());
        correctState.setSpokenByThem(sm2.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 2.0);
        testCase.getEvaluationStates().put(correctState, (float) 2.0);

        return testCase;
    }


/*  *//*
    3) deal with overanswering in clarification

    U: unclear value ()
    set up a meeting {at one, in one hour, none}
    S: request-restate-value
    When did you say?
    U: overanswer
    At 4 at Samsung
    *//*
    DSTTester testCase3() {
        YodaEnvironment yodaEnvironment;
        DSTTester testCase;
        Turn currentTurn;
        DiscourseUnit2.DialogStateHypothesis correctState;
        SemanticsModel sm1;
        SemanticsModel sm2;
        SemanticsModel sm3;
        SemanticsModel sm4;
        SemanticsModel csm1;
        SemanticsModel csm2;
        SemanticsModel csm3;
        StringDistribution sluDistribution;
        Map<String, SemanticsModel> sluHypotheses;

        yodaEnvironment = YodaEnvironment.dstTestingEnvironment();
        testCase = new DSTTester(yodaEnvironment);

        /// Turn 1
        sm1 = new SemanticsModel();
        sm1.getSlots().put("dialogAct", "Command");
        sm1.getSlots().put("action", "Create");
        sm1.getSlots().put("patient", "X");
        csm1 = new SemanticsModel();
        sm1.getChildren().put("X", csm1);
        csm1.getSlots().put("class", "Meeting");
        sm1.getSlots().put("atTime", "Y");
        csm1 = new SemanticsModel();
        sm1.getChildren().put("Y", csm1);
        csm1.getSlots().put("class", "Time");
        csm1.getSlots().put("relativity", "forward");
        csm1.getSlots().put("hour", "one");

        sm2 = new SemanticsModel();
        sm2.getSlots().put("dialogAct", "Command");
        sm2.getSlots().put("action", "Create");
        sm2.getSlots().put("patient", "X");
        csm1 = new SemanticsModel();
        sm2.getChildren().put("X", csm1);
        csm1.getSlots().put("class", "Meeting");
        sm2.getSlots().put("atTime", "Y");
        csm1 = new SemanticsModel();
        sm2.getChildren().put("Y", csm1);
        csm1.getSlots().put("class", "Time");
        csm1.getSlots().put("hour", "one");

        sm3 = new SemanticsModel();
        sm3.getSlots().put("dialogAct", "Command");
        sm3.getSlots().put("action", "Create");
        sm3.getSlots().put("patient", "X");
        csm1 = new SemanticsModel();
        sm3.getChildren().put("X", csm1);
        csm1.getSlots().put("class", "Meeting");

        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm1);
        sluHypotheses.put("hyp2", sm2);
        sluHypotheses.put("hyp3", sm3);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", .4);
        sluDistribution.put("hyp2", .3);
        sluDistribution.put("hyp3", .3);

        currentTurn = new Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByThem(sm1.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 0.0);
        testCase.getEvaluationStates().put(correctState, (float) 0.0);


        /// Turn 2
        sm4 = new SemanticsModel();
        sm4.getSlots().put("dialogAct", "RequestRole");
        sm4.getSlots().put("patient", "X");
        csm1 = new SemanticsModel();
        sm4.getChildren().put("X", csm1);
        csm1.getSlots().put("class", "RoleDescription");
        csm1.getSlots().put("roleClass", "Time");
        // path contains whatever information was given about the path to the role being requested
        csm1.getSlots().put("path", "Y");
        csm2 = new SemanticsModel();
        csm1.getChildren().put("Y", csm2);
        csm2.getSlots().put("atTime", "Z");
        csm2.getChildren().put("Z", new SemanticsModel());

        // said by the system
        SemanticsModel sm5 = new SemanticsModel();
        sm5.getSlots().put("dialogAct", "Offer");
        sm5.getSlots().put("action", "Create");
        sm5.getSlots().put("patient", "X");
        csm1 = new SemanticsModel();
        sm5.getChildren().put("X", csm1);
        csm1.getSlots().put("class", "Meeting");
        sm5.getSlots().put("atTime", "Requested");


        currentTurn = new Turn("system", sm4, null, null);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByThem(sm1.deepCopy());
        correctState.setSpokenByMe(sm5.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 1.0);
        testCase.getEvaluationStates().put(correctState, (float) 1.0);

        /// Turn 3
        SemanticsModel sm6 = new SemanticsModel();
        sm6.getSlots().put("dialogAct", "Fragment");
        sm6.getSlots().put("atTime", "X");
        csm1 = new SemanticsModel();
        sm6.getChildren().put("X", csm1);
        csm1.getSlots().put("class", "Time");
        csm1.getSlots().put("hour", "ten");
        sm6.getSlots().put("atPlace", "Y");
        csm1 = new SemanticsModel();
        sm6.getChildren().put("Y", csm1);
        csm1.getSlots().put("class", "Place");
        csm1.getSlots().put("name", "Samsung");

        SemanticsModel sm7 = new SemanticsModel();
        sm7.getSlots().put("dialogAct", "Fragment");
        sm7.getSlots().put("atTime", "X");
        csm1 = new SemanticsModel();
        sm7.getChildren().put("X", csm1);
        csm1.getSlots().put("class", "Time");
        csm1.getSlots().put("hour", "one");
        sm7.getSlots().put("atPlace", "Y");
        csm1 = new SemanticsModel();
        sm7.getChildren().put("Y", csm1);
        csm1.getSlots().put("class", "Place");
        csm1.getSlots().put("name", "Samsung");

        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm6);
        sluHypotheses.put("hyp2", sm7);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", .9);
        sluDistribution.put("hyp1", .1);

        // new ground truth
        sm1 = new SemanticsModel();
        sm1.getSlots().put("dialogAct", "Command");
        sm1.getSlots().put("action", "Create");
        sm1.getSlots().put("patient", "X");
        csm1 = new SemanticsModel();
        sm1.getChildren().put("X", csm1);
        csm1.getSlots().put("class", "Meeting");
        sm1.getSlots().put("atTime", "Y");
        csm1 = new SemanticsModel();
        sm1.getChildren().put("Y", csm1);
        csm1.getSlots().put("class", "Time");
        csm1.getSlots().put("hour", "ten");
        sm1.getSlots().put("atPlace", "Y");
        csm1 = new SemanticsModel();
        sm1.getChildren().put("Y", csm1);
        csm1.getSlots().put("class", "Place");
        csm1.getSlots().put("name", "Samsung");

        currentTurn = new Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DiscourseUnit2.DialogStateHypothesis();
        correctState.setSpokenByMe(sm5.deepCopy());
        correctState.setSpokenByThem(sm1.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 2.0);
        testCase.getEvaluationStates().put(correctState, (float) 2.0);


        return testCase;
    }*/

}
