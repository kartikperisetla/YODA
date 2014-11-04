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
public class LinearFuzzyMap implements Function{
    @Override
    public String getURI() {
        return Database.baseURI+this.getClass().getSimpleName();
    }

    @Override
    public Value evaluate(ValueFactory valueFactory, Value... values) throws ValueExprEvaluationException {
        if (values.length != 3) {
            throw new ValueExprEvaluationException(getURI()+" requires" +
                    "exactly 3 arguments, got " + values.length);
        }

        double center = ((Literal)values[0]).doubleValue();
        double slope = ((Literal)values[1]).doubleValue();
        double actual = ((Literal)values[2]).doubleValue();

        double ans = Doubles.max(1.0 - slope * (Math.abs(center - actual)), 0.0);
        return valueFactory.createLiteral(ans);
    }
}
