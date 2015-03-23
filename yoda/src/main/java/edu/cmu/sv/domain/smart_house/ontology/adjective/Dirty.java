package edu.cmu.sv.domain.smart_house.ontology.adjective;

/**
 * Created by cohend on 3/4/15.
 */
public class Dirty extends CleanlinessAdjective {
    @Override
    public double getCenter() {
        return 0;
    }

    @Override
    public double getSlope() {
        return 1;
    }
}


