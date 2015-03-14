package edu.cmu.sv.domain.smart_house.GUI;

import edu.cmu.sv.database.Database;
import edu.cmu.sv.yoda_environment.MongoLogHandler;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

import java.util.*;

public class Simulator {
	private static Set<GUIThing> thingsInSimulation;
	
	static {
		thingsInSimulation = new HashSet<>();
		GUIRoom room1 = new GUIRoom("Kitchen", Database.baseURI+"0000");
		GUIRoom room2 = new GUIRoom("Living Room", "0001");
		GUIPerson jane = new GUIPerson("Jane", "F", room1, "0002");
		GUIPerson john = new GUIPerson("John", "M", room2, "0003");
		GUIMicrowave sonyMicrowave = new GUIMicrowave("Sony Microwave", room1, true, "0004");
		GUIMicrowave samsungMicrowave = new GUIMicrowave("Samsung Microwave", room1, false, "0005");
		GUIThermostat nest = new GUIThermostat("Nest", room2, true, "0006");
		GUISecuritySystem comcastSec = new GUISecuritySystem("Comcast Security System", room2, true, "0007");
		GUIAC panasonicAC = new GUIAC("Panasonic AC", room2, true, "0008");
		thingsInSimulation.add(jane);
		thingsInSimulation.add(john);
		thingsInSimulation.add(room1);
		thingsInSimulation.add(room2);
		thingsInSimulation.add(sonyMicrowave);
		thingsInSimulation.add(samsungMicrowave);
		thingsInSimulation.add(nest);
		thingsInSimulation.add(panasonicAC);
		thingsInSimulation.add(comcastSec);

        for(GUIThing thing : thingsInSimulation) {
            String insertString = Database.prefixes + "INSERT DATA {";
            insertString += "<" + thing.getCorrespondingURI() + "> base:power_state \"" + ((GUIElectronic) thing).getState() + "\"^^xsd:string.\n";
            insertString += "}";
            Database.getLogger().info(MongoLogHandler.createSimpleRecord("Insert thing", insertString).toJSONString());
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
	
	public static Set<GUIThing> getThings() {
		return thingsInSimulation;
	}
}