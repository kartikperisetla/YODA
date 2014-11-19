package edu.cmu.sv.database;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/29/14.
 */
public class DatabaseRegistry {
    public static Set<String> nonOntologyRelations = new HashSet<>();
    public static Set<String> turtleDatabaseSources = new HashSet<>();

    static {
        // point of interest database
        turtleDatabaseSources.add("./src/resources/poi.turtle");

        nonOntologyRelations.add("gps_lon");
        nonOntologyRelations.add("gps_lat");
        nonOntologyRelations.add("expensiveness");

    }

}
