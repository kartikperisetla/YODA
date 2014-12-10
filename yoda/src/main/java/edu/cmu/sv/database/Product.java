package edu.cmu.sv.database;

import com.google.common.primitives.Doubles;
import org.kohsuke.MetaInfServices;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.query.algebra.evaluation.function.Function;

/**
 * Created by David Cohen on 11/2/14.
 */
@MetaInfServices
public class Product implements Function{
    @Override
    public String getURI() {
        return Database.baseURI+this.getClass().getSimpleName();
    }

    @Override
    public Value evaluate(ValueFactory valueFactory, Value... values) throws ValueExprEvaluationException {
//        if (values.length < 1) {
//            throw new ValueExprEvaluationException(getURI()+" requires" +
//                    "at least 1 argument, got " + values.length);
//        }
        double ans = 1.0;
        for (int i = 0; i < values.length; i++) {
            ans *= ((Literal)values[i]).doubleValue();
        }
        return valueFactory.createLiteral(ans);
    }
}
