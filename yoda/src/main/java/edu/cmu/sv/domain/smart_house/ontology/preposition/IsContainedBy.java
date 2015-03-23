package edu.cmu.sv.domain.smart_house.ontology.preposition;

/**
 * Created by David Cohen on 10/30/14.
 *
 * Relates two POI's that are geographically close to each other.
 */
public class IsContainedBy extends ContainedByPreposition {
    @Override
    public double getCenter() {
        return 1;
    }

    @Override
    public double getSlope() {
        return 100;
    }
}
