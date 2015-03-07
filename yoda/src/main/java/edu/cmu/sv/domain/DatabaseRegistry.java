package edu.cmu.sv.domain;

import edu.cmu.sv.database.Sensor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/29/14.
 */
public class DatabaseRegistry {
    public Set<String> nonOntologyRelations = new HashSet<>();
    public Set<String> turtleDatabaseSources = new HashSet<>();
    public Set<Sensor> sensors = new HashSet<>();



}
