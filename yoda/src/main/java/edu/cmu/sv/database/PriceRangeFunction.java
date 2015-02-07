package edu.cmu.sv.database;

import org.kohsuke.MetaInfServices;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;
import org.openrdf.query.algebra.evaluation.function.Function;

/**
 * Created by David Cohen on 11/2/14.
 * Function to compute the transient distance between two physical objects.
 * NOTE: this uses 1- exp(dist) to normalize values between 0 and 1
 */
@MetaInfServices
public class PriceRangeFunction implements Function{
    @Override
    public String getURI() {
        return Database.baseURI+this.getClass().getSimpleName();
    }

    @Override
    public Value evaluate(ValueFactory valueFactory, Value... values) throws ValueExprEvaluationException {
        if (values.length != 1) {
            throw new ValueExprEvaluationException(getURI()+" requires" +
                    "exactly 1 arguments, got " + values.length);
        }

        int priceRange = ((Literal)values[0]).intValue();
        return valueFactory.createLiteral(priceRange / 4.0);
    }
}
