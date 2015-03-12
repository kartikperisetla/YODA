package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.database.Sensor;
import edu.cmu.sv.domain.DatabaseRegistry;
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
public class SmartHouseDatabaseRegistry extends DatabaseRegistry {
    public SmartHouseDatabaseRegistry() {
        turtleDatabaseSources.add("./src/resources/home.turtle");
        sensors.add(new ApplianceSensor());
    }

    public class ApplianceSensor implements Sensor{
        @Override
        public void sense(YodaEnvironment targetEnvironment) {
            String pS_0000 = HouseSimulation.POI_0000_powerState;
            String pS_0001 = HouseSimulation.POI_0001_powerState;
            String uri0 = Database.baseURI+"POI_0000";
            String uri1 = Database.baseURI+"POI_0001";

            synchronized (targetEnvironment.db.connection) {
                // clear existing power state
                {
                    String deleteString = Database.prefixes;
                    deleteString += "DELETE {<" + uri0 + "> base:power_state ?y }";
                    deleteString += "WHERE {<" + uri0 + "> base:power_state ?y }";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("clear appliance power state", deleteString).toJSONString());
                    try {
                        Update update = targetEnvironment.db.connection.prepareUpdate(
                                QueryLanguage.SPARQL, deleteString, Database.dstFocusURI);
                        update.execute();
                    } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
                {
                    String deleteString = Database.prefixes;
                    deleteString += "DELETE {<" + uri1 + "> base:power_state ?y }";
                    deleteString += "WHERE {<" + uri1 + "> base:power_state ?y }";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("clear appliance power state", deleteString).toJSONString());
                    try {
                        Update update = targetEnvironment.db.connection.prepareUpdate(
                                QueryLanguage.SPARQL, deleteString, Database.dstFocusURI);
                        update.execute();
                    } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }

                // set new power state
                {
                    String insertString = Database.prefixes + "INSERT DATA {";
                    insertString += "<" + uri0 + "> base:power_state \"" + pS_0000 + "\"^^xsd:string.\n";
                    insertString += "}";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("Turn on appliance", insertString).toJSONString());
                    try {
                        Update update = targetEnvironment.db.connection.prepareUpdate(
                                QueryLanguage.SPARQL, insertString, Database.dstFocusURI);
                        update.execute();
                    } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
                {
                    String insertString = Database.prefixes + "INSERT DATA {";
                    insertString += "<" + uri1 + "> base:power_state \"" + pS_0001 + "\"^^xsd:string.\n";
                    insertString += "}";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("Turn on appliance", insertString).toJSONString());
                    try {
                        Update update = targetEnvironment.db.connection.prepareUpdate(
                                QueryLanguage.SPARQL, insertString, Database.dstFocusURI);
                        update.execute();
                    } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            }


        }
    }
}

