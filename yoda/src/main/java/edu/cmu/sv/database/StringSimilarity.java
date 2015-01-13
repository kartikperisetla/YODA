package edu.cmu.sv.database;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.openrdf.model.Literal;
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
//        System.out.println("s1:"+s1+", s2:"+s2);
        if (s1.length()==0 || s2.length()==0){
            return valueFactory.createLiteral(0.0);
        }

//        double levenshteinSimilarity = 1.0 - (1.0*StringUtils.getLevenshteinDistance(s1.toLowerCase(), s2.toLowerCase()) / (Integer.max(s1.length(),s2.length())));
        double jaroWinklerSimilarity = StringUtils.getJaroWinklerDistance(s1.toLowerCase(), s2.toLowerCase());
        return valueFactory.createLiteral(Math.pow(jaroWinklerSimilarity,3));
    }
}
