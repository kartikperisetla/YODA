package edu.cmu.sv.domain.smart_house.ontology.adjective;

/**
 * Created by cohend on 3/4/15.
 */
public class Clean extends CleanlinessAdjective {
    @Override
    public double getCenter() {
        return 1;
    }

    @Override
    public double getSlope() {
        return 2;
    }
}

