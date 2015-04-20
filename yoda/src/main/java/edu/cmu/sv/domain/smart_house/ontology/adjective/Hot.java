package edu.cmu.sv.domain.smart_house.ontology.adjective;

/**
 * Created by dan on 4/15/15.
 */
public class Hot extends TemperatureAdjective {
    @Override
    public double getCenter() {
        return 0;
    }

    @Override
    public double getSlope() {
        return 1;
    }
}
