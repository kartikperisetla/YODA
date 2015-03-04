package edu.cmu.sv;

import edu.cmu.sv.natural_language_generation.CorpusGeneration;
import edu.cmu.sv.semantics.SemanticsModel;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by David Cohen on 10/29/14.
 */
public class GenerateCorpus {

    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("generating corpus...");
        List<Map.Entry<String, SemanticsModel>> corpus = CorpusGeneration.generateCorpus2();
        System.out.println("corpus size:"+corpus.size());
        corpus.forEach(x -> System.out.print("---\n"+x.getKey()+"\n"+x.getValue().getInternalRepresentation().toJSONString()+"\n"));

    }
}
