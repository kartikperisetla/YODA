package edu.cmu.sv.spoken_language_understanding;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by David Cohen on 12/29/14.
 */
public class Tokenizer {
    public static List<String> tokenize(String inputString){
        String[] tmp = inputString.toLowerCase().split(" ");
        return new LinkedList<>(Arrays.asList(tmp));
    }
}
