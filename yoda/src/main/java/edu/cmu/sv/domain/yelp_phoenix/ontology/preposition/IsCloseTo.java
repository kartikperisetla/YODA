package edu.cmu.sv.domain.yelp_phoenix.ontology.preposition;

/**
 * Created by David Cohen on 10/30/14.
 *
 * Relates two POI's that are geographically close to each other.
 */
public class IsCloseTo extends DistancePreposition {
    @Override
    public double getCenter() {
        return 0;
    }

    @Override
    public double getSlope() {
        return 2;
    }
}
