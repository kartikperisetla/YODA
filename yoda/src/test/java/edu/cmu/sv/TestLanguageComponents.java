package edu.cmu.sv;

import edu.cmu.sv.semantics.SemanticsModel;
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
        MultiClassifier.loadPreferences();
        MultiClassifier classifier = new MultiClassifier();
        System.out.println("is red rock expensive:");
        NodeMultiClassificationProblem problem = new NodeMultiClassificationProblem("is red rock expensive", SemanticsModel.parseJSON("{}"), "");
        System.out.println("classifying problem...");
        classifier.classify(problem);
        classifier.classify(problem);
        classifier.classify(problem);
        classifier.classify(problem);

    }
}
