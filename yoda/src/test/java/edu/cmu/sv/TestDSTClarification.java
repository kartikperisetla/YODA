package edu.cmu.sv;

import edu.cmu.sv.dialog_state_tracking.DSTTester;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.StringDistribution;
import org.junit.Test;

import java.util.*;

/**
 * Created by David Cohen on 9/17/14.
 *
 * Run test cases to demonstrate basic clarification dialog state tracking behavior.
 *
 */
public class TestDSTClarification {


    @Test
    public void Test() {
        // Each test case is fairly complicated,
        // so to reduce the chance of making variable scope mistakes,
        // I made a separate function for each one
        for (DSTTester testDialog : Arrays.asList(testCase1(), testCase2(), testCase3())) {
            System.out.println(testDialog.evaluate());
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
    */
    DSTTester testCase1() {
        DSTTester testCase;
        DSTTester.Turn currentTurn;
        DSTTester.EvaluationState correctState;
        SemanticsModel sm1;
        SemanticsModel sm2;
        SemanticsModel sm3;
        SemanticsModel sm4;
        SemanticsModel childSM;
        SemanticsModel grandChildSM;
        StringDistribution sluDistribution;
        Map<String, SemanticsModel> sluHypotheses;

        testCase = new DSTTester();

        /// Turn 1
        sm1 = new SemanticsModel();
        sm1.getSlots().put("dialogAct", "Command");
        sm1.getSlots().put("action", "Create");
        sm1.getSlots().put("patient", "X");
        childSM = new SemanticsModel();
        sm1.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Meeting");
        sm1.getSlots().put("atTime", "Y");
        childSM = new SemanticsModel();
        sm1.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("hour", "one");

        sm2 = new SemanticsModel();
        sm2.getSlots().put("dialogAct", "Command");
        sm2.getSlots().put("action", "Create");
        sm2.getSlots().put("patient", "X");
        childSM = new SemanticsModel();
        sm2.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Meeting");
        sm2.getSlots().put("atTime", "Y");
        childSM = new SemanticsModel();
        sm2.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("hour", "ten");

        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm1);
        sluHypotheses.put("hyp2", sm2);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", .6);
        sluDistribution.put("hyp2", .4);

        currentTurn = new DSTTester.Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DSTTester.EvaluationState(sm1.deepCopy(), new SemanticsModel(), new SemanticsModel());
        testCase.getTurns().put(currentTurn, (float) 0.0);
        testCase.getEvaluationStates().put(correctState, (float) 0.0);


        /// Turn 2
        sm3 = new SemanticsModel();
        sm3.getSlots().put("dialogAct", "RequestDisambiguateValues");
        sm3.getSlots().put("atTime", "X");
        childSM = new SemanticsModel();
        sm3.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Or");
        childSM.getSlots().put("option1", "Y");
        childSM.getSlots().put("option2", "Z");
        grandChildSM = new SemanticsModel();
        grandChildSM.getSlots().put("class", "Time");
        grandChildSM.getSlots().put("hour", "one");
        childSM.getChildren().put("Y", grandChildSM);
        grandChildSM = new SemanticsModel();
        grandChildSM.getSlots().put("class", "Time");
        grandChildSM.getSlots().put("hour", "ten");
        childSM.getChildren().put("Z", grandChildSM);

        currentTurn = new DSTTester.Turn("system", sm3.deepCopy(), null, null);
        correctState = new DSTTester.EvaluationState(sm1.deepCopy(), sm3.deepCopy(), sm3.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 1.0);
        testCase.getEvaluationStates().put(correctState, (float) 1.0);


        /// Turn 3
        sm4 = new SemanticsModel();
        sm4.getSlots().put("dialogAct", "Fragment");
        sm4.getSlots().put("atTime", "X");
        childSM = new SemanticsModel();
        sm4.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("hour", "ten");

        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm4);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", 1.0);

        currentTurn = new DSTTester.Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DSTTester.EvaluationState(sm2.deepCopy(), sm3.deepCopy(), sm3.deepCopy());
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
    DSTTester testCase2() {
        DSTTester testCase;
        DSTTester.Turn currentTurn;
        DSTTester.EvaluationState correctState;
        SemanticsModel sm1;
        SemanticsModel sm2;
        SemanticsModel sm3;
        SemanticsModel sm4;
        SemanticsModel childSM;
        SemanticsModel grandChildSM;
        StringDistribution sluDistribution;
        Map<String, SemanticsModel> sluHypotheses;

        testCase = new DSTTester();

        /// Turn 1
        sm1 = new SemanticsModel();
        sm1.getSlots().put("dialogAct", "Statement");
        sm1.getSlots().put("state", "exists");
        sm1.getSlots().put("patient", "X");
        childSM = new SemanticsModel();
        sm1.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Meeting");
        sm1.getSlots().put("atTime", "Y");
        childSM = new SemanticsModel();
        sm1.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("hour", "two");

        currentTurn = new DSTTester.Turn("system", sm1.deepCopy(), null, null);
        correctState = new DSTTester.EvaluationState(new SemanticsModel(), sm1.deepCopy(), sm1.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 0.0);
        testCase.getEvaluationStates().put(correctState, (float) 0.0);

        /// Turn 2
        sm2 = new SemanticsModel();
        sm2.getSlots().put("dialogAct", "NonHearing");

        sm3 = new SemanticsModel();
        sm3.getSlots().put("dialogAct", "NonUnderstanding");

        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm1);
        sluHypotheses.put("hyp2", sm2);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", .6);
        sluDistribution.put("hyp2", .4);

        currentTurn = new DSTTester.Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DSTTester.EvaluationState(sm2.deepCopy(), new SemanticsModel(), sm1.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 1.0);
        testCase.getEvaluationStates().put(correctState, (float) 1.0);

        /// Turn 3
        // (the system repeats itself)
        currentTurn = new DSTTester.Turn("system", sm1.deepCopy(), null, null);
        correctState = new DSTTester.EvaluationState(sm2.deepCopy(), sm1.deepCopy(), sm1.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 2.0);
        testCase.getEvaluationStates().put(correctState, (float) 2.0);

        return testCase;
    }


    /*
    3) deal with overanswering in clarification

    U: unclear value ()
    set up a meeting {at one, in one hour, none}
    S: request-restate-value
    When did you say?
    U: overanswer
    At 4 at Samsung
    */
    DSTTester testCase3() {
        DSTTester testCase;
        DSTTester.Turn currentTurn;
        DSTTester.EvaluationState correctState;
        SemanticsModel sm1;
        SemanticsModel sm2;
        SemanticsModel sm3;
        SemanticsModel sm4;
        SemanticsModel childSM;
        SemanticsModel grandChildSM;
        StringDistribution sluDistribution;
        Map<String, SemanticsModel> sluHypotheses;

        testCase = new DSTTester();

        /// Turn 1
        sm1 = new SemanticsModel();
        sm1.getSlots().put("dialogAct", "Command");
        sm1.getSlots().put("action", "Create");
        sm1.getSlots().put("patient", "X");
        childSM = new SemanticsModel();
        sm1.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Meeting");
        sm1.getSlots().put("atTime", "Y");
        childSM = new SemanticsModel();
        sm1.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("relativity", "forward");
        childSM.getSlots().put("hour", "one");

        sm2 = new SemanticsModel();
        sm2.getSlots().put("dialogAct", "Command");
        sm2.getSlots().put("action", "Create");
        sm2.getSlots().put("patient", "X");
        childSM = new SemanticsModel();
        sm2.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Meeting");
        sm2.getSlots().put("atTime", "Y");
        childSM = new SemanticsModel();
        sm2.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("hour", "one");

        sm3 = new SemanticsModel();
        sm3.getSlots().put("dialogAct", "Command");
        sm3.getSlots().put("action", "Create");
        sm3.getSlots().put("patient", "X");
        childSM = new SemanticsModel();
        sm3.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Meeting");
        
        sluHypotheses = new HashMap<>();
        sluHypotheses.put("hyp1", sm1);
        sluHypotheses.put("hyp2", sm2);
        sluHypotheses.put("hyp3", sm3);
        sluDistribution = new StringDistribution();
        sluDistribution.put("hyp1", .4);
        sluDistribution.put("hyp2", .3);
        sluDistribution.put("hyp3", .3);

        currentTurn = new DSTTester.Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DSTTester.EvaluationState(sm1.deepCopy(), new SemanticsModel(), new SemanticsModel());
        testCase.getTurns().put(currentTurn, (float) 0.0);
        testCase.getEvaluationStates().put(correctState, (float) 0.0);


        /// Turn 2
        sm4 = new SemanticsModel();
        sm4.getSlots().put("dialogAct", "RequestRole");
        sm4.getSlots().put("patient", "X");
        childSM = new SemanticsModel();
        sm4.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "RoleDescription");
        childSM.getSlots().put("roleClass", "Time");
        // path contains whatever information was given about the path to the role being requested
        childSM.getSlots().put("path", "Y"); 
        grandChildSM = new SemanticsModel();
        childSM.getChildren().put("Y", grandChildSM);
        grandChildSM.getSlots().put("atTime", "Z");
        grandChildSM.getChildren().put("Z", new SemanticsModel());

        // said by the system / understood by user
        SemanticsModel sm5 = new SemanticsModel();
        sm5.getSlots().put("dialogAct", "Offer");
        sm5.getSlots().put("action", "Create");
        sm5.getSlots().put("patient", "X");
        childSM = new SemanticsModel();
        sm5.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Meeting");
        sm5.getSlots().put("atTime", "<REQUESTED>");
        
        
        currentTurn = new DSTTester.Turn("system", sm4, null, null);
        correctState = new DSTTester.EvaluationState(sm1.deepCopy(), sm5.deepCopy(), sm5.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 1.0);
        testCase.getEvaluationStates().put(correctState, (float) 1.0);

        /// Turn 3
        SemanticsModel sm6 = new SemanticsModel();
        sm6.getSlots().put("dialogAct", "Fragment");
        sm6.getSlots().put("atTime", "X");
        childSM = new SemanticsModel();
        sm6.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("hour", "ten");
        sm6.getSlots().put("atPlace", "Y");
        childSM = new SemanticsModel();
        sm6.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Place");
        childSM.getSlots().put("name", "Samsung");

        SemanticsModel sm7 = new SemanticsModel();
        sm7.getSlots().put("dialogAct", "Fragment");
        sm7.getSlots().put("atTime", "X");
        childSM = new SemanticsModel();
        sm7.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("hour", "one");
        sm7.getSlots().put("atPlace", "Y");
        childSM = new SemanticsModel();
        sm7.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Place");
        childSM.getSlots().put("name", "Samsung");

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
        childSM = new SemanticsModel();
        sm1.getChildren().put("X", childSM);
        childSM.getSlots().put("class", "Meeting");
        sm1.getSlots().put("atTime", "Y");
        childSM = new SemanticsModel();
        sm1.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Time");
        childSM.getSlots().put("hour", "ten");
        sm1.getSlots().put("atPlace", "Y");
        childSM = new SemanticsModel();
        sm1.getChildren().put("Y", childSM);
        childSM.getSlots().put("class", "Place");
        childSM.getSlots().put("name", "Samsung");

        currentTurn = new DSTTester.Turn("user", null, sluHypotheses, sluDistribution);
        correctState = new DSTTester.EvaluationState(sm1.deepCopy(), sm5.deepCopy(), sm5.deepCopy());
        testCase.getTurns().put(currentTurn, (float) 2.0);
        testCase.getEvaluationStates().put(correctState, (float) 2.0);
        
        
        return testCase;
    }

}
