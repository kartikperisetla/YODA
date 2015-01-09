package edu.cmu.sv;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.*;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by David Cohen on 10/29/14.
 *
 * Generate an artificial corpus and use it to train language components (SLU / LM)
 */
public class TestLanguageComponents {
    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();
        NestedChunkingUnderstander.start(yodaEnvironment);

        List<String> testUtterances = new LinkedList<>();
        testUtterances.add("is the chinese restaurant near red rock expensive");
        testUtterances.add("how expensive is the chinese restaurant near red rock");
        testUtterances.add("yes");
        testUtterances.add("no");
        testUtterances.add("harvard law school");
        testUtterances.add("is expensive");
        testUtterances.add("give me directions to castro street");
        testUtterances.add("directions");

        for (String testUtterance : testUtterances){
            System.out.println("understanding utterance: "+testUtterance);
            NestedChunkingUnderstander.understand(testUtterance);
        }

//        testIndividualComponents();
    }


    public void testIndividualComponents(){
        MultiClassifier.loadPreferences();
        MultiClassifier classifier = new MultiClassifier();
        Chunker.loadPreferences();
        Chunker chunker = new Chunker();

        classifyDialogAct(classifier, "is red rock expensive");
        classifyDialogAct(classifier, "where is red rock");

        chunkUtterance(chunker, "is Starbucks expensive");
        chunkUtterance(chunker, "is the restaurant near to hollywood");
        chunkUtterance(chunker, "the church near mcdonalds");

        classifyDialogAct(classifier, "the church on castro street");
        classifyDialogAct(classifier, "give me directions");

        chunkUtterance(chunker, "give me directions");
        chunkUtterance(chunker, "how close to red rock is moffett field");
        chunkUtterance(chunker, "how close is red rock to moffett field");
        chunkUtterance(chunker, "the school");
    }

    public void classifyDialogAct(MultiClassifier classifier, String utterance){
            System.out.println(utterance);
            NodeMultiClassificationProblem problem = new NodeMultiClassificationProblem(utterance, SemanticsModel.parseJSON("{}"), "");
            classifier.classify(problem);
    }

    public void chunkUtterance(Chunker chunker, String utterance){
        System.out.println(utterance);
        ChunkingProblem chunkingProblem = new ChunkingProblem(utterance, SemanticsModel.parseJSON("{}"), "", null);
        chunker.chunk(chunkingProblem);
    }

}
