package edu.cmu.sv.spoken_language_understanding;

import com.google.common.primitives.Doubles;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David Cohen on 5/11/15.
 */
public class Utils {
    public static double stringSetBestCoverage(String phrase, Set<String> matchingStrings){
        double ans = 0.0;
        int adjustedLength = phrase.replaceAll("\\Aa ","").replaceAll("\\Aan ","").replaceAll("\\Aany ","").
                replaceAll("\\Athe ","").replaceAll("\\Asome ","").trim().length();
        for (String matchingString : matchingStrings) {
            Pattern regexPattern = Pattern.compile("(.+ | |)" + matchingString + "( .+| |)");
            Matcher matcher = regexPattern.matcher(phrase);
            if (matcher.matches())
                ans = Doubles.max(ans, matchingString.length() * 1.0 / adjustedLength);
            }
        return Doubles.min(ans, 1.0);
    }

    public static double stringSetTotalCoverage(String phrase, Set<String> matchingStrings){
        phrase = phrase.replace("any ","").replace("the ","").replace("some ","").trim();
        int adjustedLength = Integer.max(1, phrase.length());
        for (String matchingString : matchingStrings) {
            phrase = phrase.replaceAll("(\\A| )" + matchingString + "( |\\z)", " ").trim();
        }
        return 1.0 - ((1.0 * phrase.length()) / adjustedLength);
    }


}
