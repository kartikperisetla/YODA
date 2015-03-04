package edu.cmu.sv.domain.yelp_phoenix.ontology.adjective;

/**
 * Created by David Cohen on 11/2/14.
 */
public class Expensive extends ExpensivenessAdjective {
    @Override
    public double getCenter() {
        return 1;
    }

    @Override
    public double getSlope() {
        return 1;
    }

}
