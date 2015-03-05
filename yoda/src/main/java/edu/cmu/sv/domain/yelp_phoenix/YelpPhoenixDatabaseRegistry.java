package edu.cmu.sv.domain.yelp_phoenix;

import edu.cmu.sv.domain.DatabaseRegistry;

/**
 * Created by David Cohen on 3/4/15.
 */
public class YelpPhoenixDatabaseRegistry extends DatabaseRegistry {
    public YelpPhoenixDatabaseRegistry() {
        turtleDatabaseSources.add("./src/resources/yelp_business.turtle");
    }
}

