package edu.cmu.sv;

import edu.cmu.sv.utils.StringDistribution;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by David Cohen on 10/29/14.
 *
 * Generate an artificial corpus and use it to train language components (SLU / LM)
 */

public class TestUtils {

    /*
    * Can only create one yoda environment per program, since it relies on static classes
    * */
    @Test
    public void Test() throws FileNotFoundException, UnsupportedEncodingException {
        StringDistribution d = new StringDistribution();
        d.put("a", 1.0);
        d.put("b", 0.0);
        d.put("c", -10.0);
        d.normalize();
    }


}