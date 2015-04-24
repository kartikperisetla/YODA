package edu.cmu.sv.spoken_language_understanding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 12/29/14.
 */
public class Tokenizer {
    public static List<String> tokenize(String inputString){
        Pattern p = Pattern.compile("(\\p{Alpha}+)(\\p{Digit}+)*");
        Matcher m = p.matcher(inputString.toLowerCase());
        List<String> tmp = new ArrayList<>();

        System.out.println(m.matches());
        if (m.matches()) {
            while(m.find()) {
                System.out.println(m.group(1));
                System.out.println(m.group(2));
                tmp.add(m.group(1) + m.group(2));
            }
        } else {
            tmp = Arrays.asList(inputString.toLowerCase().split(" "));
        }
        return new LinkedList<>(tmp);
    }
}
