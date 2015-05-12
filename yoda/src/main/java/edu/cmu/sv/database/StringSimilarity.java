package edu.cmu.sv.database;

import com.google.common.primitives.Doubles;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.query.algebra.evaluation.function.Function;

/**
 * Created by David Cohen on 11/2/14.
 *
 * Returns 0-1, trying to estimate p(use name 1 | actual name 2)
 *
 */
@MetaInfServices
public class StringSimilarity implements Function{
    public static double possibleMatchThreshold = .2;

    @Override
    public String getURI() {
        return Database.baseURI+this.getClass().getSimpleName();
    }

    @Override
    public Value evaluate(ValueFactory valueFactory, Value... values) throws ValueExprEvaluationException {
        if (values.length != 2) {
            throw new ValueExprEvaluationException(getURI()+" requires" +
                    "exactly 2 arguments, got " + values.length);
        }

        String s1 = (values[0]).stringValue();
        String s2 = (values[1]).stringValue();
        return valueFactory.createLiteral(similarityHelper(s1, s2));
    }

    public static double similarityHelper(String s1, String s2){
        double maxSimilarity = 0.0;
        s1 = s1.toLowerCase().trim();
        s2 = s2.toLowerCase().trim();

        if (s1.length()==0 || s2.length()==0) {
            return 0.0;
        }

            // initial raw similarity
        maxSimilarity = Doubles.max(maxSimilarity, StringUtils.getJaroWinklerDistance(s1, s2));

        // remove 'the'
        s1 = s1.replaceAll("\\Athe ","");
        s2 = s2.replaceAll("\\Athe ","");
        maxSimilarity = Doubles.max(maxSimilarity, StringUtils.getJaroWinklerDistance(s1, s2));

        // detect and compare as acronyms, require 2 letters
        String acronymRegex ="(\\p{Alpha} )+\\p{Alpha}";
        String s1a, s2a;
        if (s1.matches(acronymRegex) || s2.matches(acronymRegex)){
            s1a = s1.contains(" ") ? s1.replaceAll("(?<=\\p{Alpha})\\p{Alpha}+(?=( |\\z))", "").replaceAll(" ","") : s1;
            s2a = s2.contains(" ") ? s2.replaceAll("(?<=\\p{Alpha})\\p{Alpha}+(?=( |\\z))", "").replaceAll(" ","") : s2;
//            System.out.println(s1a);
//            System.out.println(s2a);
            maxSimilarity = Doubles.max(maxSimilarity, .95 * StringUtils.getJaroWinklerDistance(s1a, s2a));
        }

        return Math.pow(maxSimilarity,10);
    }
}
