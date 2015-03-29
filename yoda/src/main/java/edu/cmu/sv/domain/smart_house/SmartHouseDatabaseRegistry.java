package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.database.Sensor;
import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.smart_house.GUI.GUIElectronic;
import edu.cmu.sv.domain.smart_house.GUI.GUIRoom;
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
public class SmartHouseDatabaseRegistry extends DatabaseRegistry {
    public SmartHouseDatabaseRegistry() {
        turtleDatabaseSources.add("./src/resources/home.turtle");
        sensors.add(new RoomDustLevelSensor());
        sensors.add(new AppliancePowerStateSensor());
        sensors.add(new ThingLocationSensor());
    }


    public class RoomDustLevelSensor implements Sensor{
        @Override
        public void sense(YodaEnvironment targetEnvironment) {
            synchronized (targetEnvironment.db.connection) {
                for (GUIThing thing : Simulator.getThings()){
                    if (!(thing instanceof GUIRoom))
                        continue;

                    // clear existing dust level
                    String deleteString = Database.prefixes;
                    deleteString += "DELETE {<" + thing.getCorrespondingURI() + "> base:dust_level ?y }";
                    deleteString += "WHERE {<" + thing.getCorrespondingURI() + "> base:dust_level ?y }";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("clear room's dust level", deleteString).toJSONString());
                    try {
                        Update update = targetEnvironment.db.connection.prepareUpdate(
                                QueryLanguage.SPARQL, deleteString, Database.dstFocusURI);
                        update.execute();
                    } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }

                    // set new dust level
                    int newDustLevel = ((GUIRoom) thing).getDustLevel();
                    String insertString = Database.prefixes + "INSERT DATA {";
                    insertString += "<" + thing.getCorrespondingURI() + "> base:dust_level " + newDustLevel + ".\n";
                    insertString += "}";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("sensing room's dust level", insertString).toJSONString());
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


    public class ThingLocationSensor implements Sensor{
        @Override
        public void sense(YodaEnvironment targetEnvironment) {
            synchronized (targetEnvironment.db.connection) {
                for (GUIThing thing : Simulator.getThings()){
                    if (thing.getRoom()==null)
                        continue;

                    // clear existing room
                    String deleteString = Database.prefixes;
                    deleteString += "DELETE {<" + thing.getCorrespondingURI() + "> base:in_room ?y }";
                    deleteString += "WHERE {<" + thing.getCorrespondingURI() + "> base:in_room ?y }";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("clear thing's room", deleteString).toJSONString());
                    try {
                        Update update = targetEnvironment.db.connection.prepareUpdate(
                                QueryLanguage.SPARQL, deleteString, Database.dstFocusURI);
                        update.execute();
                    } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }

                    // set new room
                    String insertString = Database.prefixes + "INSERT DATA {";
                    insertString += "<" + thing.getCorrespondingURI() + "> base:in_room <" + thing.getRoom().getCorrespondingURI() + "> .\n";
                    insertString += "}";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("sensing thing's room", insertString).toJSONString());
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

    public class AppliancePowerStateSensor implements Sensor{
        @Override
        public void sense(YodaEnvironment targetEnvironment) {
            synchronized (targetEnvironment.db.connection) {
                for (GUIThing thing : Simulator.getThings()){
                    if (!(thing instanceof GUIElectronic))
                        continue;

                    // clear existing power state
                    String deleteString = Database.prefixes;
                    deleteString += "DELETE {<" + thing.getCorrespondingURI() + "> base:power_state ?y }";
                    deleteString += "WHERE {<" + thing.getCorrespondingURI() + "> base:power_state ?y }";
                    Database.getLogger().info(MongoLogHandler.createSimpleRecord("clear appliance power state", deleteString).toJSONString());
                    try {
                        Update update = targetEnvironment.db.connection.prepareUpdate(
                                QueryLanguage.SPARQL, deleteString, Database.dstFocusURI);
                        update.execute();
                    } catch (RepositoryException | UpdateExecutionException | MalformedQueryException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }

                    // set new power state
                    String powerState = ((GUIElectronic) thing).getState() ? "on" : "off";
                    String insertString = Database.prefixes + "INSERT DATA {";
                    insertString += "<" + thing.getCorrespondingURI() + "> base:power_state \"" + powerState + "\"^^xsd:string.\n";
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

