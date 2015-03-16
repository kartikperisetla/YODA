package edu.cmu.sv.domain.scotty;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.database.Sensor;
import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.smart_house.GUI.GUIElectronic;
import edu.cmu.sv.domain.smart_house.GUI.GUIThing;
import edu.cmu.sv.domain.smart_house.GUI.Simulator;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

/**
 * Created by David Cohen on 3/4/15.
 */
public class ScottyDatabaseRegistry extends DatabaseRegistry {
    public ScottyDatabaseRegistry() {
        turtleDatabaseSources.add("./src/resources/poi.turtle");
    }
}

