package edu.cmu.sv;

import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.Chunker;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.ChunkingProblem;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.MultiClassifier;
import edu.cmu.sv.spoken_language_understanding.nested_chunking_understander.NodeMultiClassificationProblem;
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
//        MultiClassifier.loadPreferences();
//        MultiClassifier classifier = new MultiClassifier();
//
//        classifyDialogAct(classifier, "is red rock expensive");
//        classifyDialogAct(classifier, "where is red rock");
//        classifyDialogAct(classifier, "the church on castro street");
//        classifyDialogAct(classifier, "give me directions");


        Chunker.loadPreferences();
        Chunker chunker = new Chunker();

        chunkUtterance(chunker, "is Starbucks expensive");
        chunkUtterance(chunker, "is the restaurant next to hollywood");
        chunkUtterance(chunker, "the church near mcdonalds");
        chunkUtterance(chunker, "give me directions");

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
