package edu.cmu.sv.domain.smart_house.GUI;

import edu.cmu.sv.database.Database;

import java.util.HashSet;
import java.util.Set;

public class Simulator {
	private static Set<GUIThing> thingsInSimulation;
	
	static {
        String prefix = Database.baseURI;
		thingsInSimulation = new HashSet<>();
		GUIRoom room1 = new GUIRoom("Kitchen", prefix+"0000", 5, 99);
		GUIRoom room2 = new GUIRoom("Living Room", prefix+"0001", 5, 84);
		GUIPerson jane = new GUIPerson("Jane", "F", room1, prefix+"0002");
		GUIPerson john = new GUIPerson("John", "M", room2, prefix+"0003");
		GUIMicrowave sonyMicrowave = new GUIMicrowave("Sony Microwave", room1, true, prefix+"0004");
		GUIThermostat nest = new GUIThermostat("Nest", room2, true, prefix+"0005");
		GUISecuritySystem comcastSec = new GUISecuritySystem("Comcast Security System", room2, true, prefix+"0006");
		GUIAC airConditioner = new GUIAC("Air Conditioner", room2, true, prefix+"0007");
        GUIRoomba roomba = new GUIRoomba("roomba", room2, prefix+"0008");
		thingsInSimulation.add(jane);
		thingsInSimulation.add(john);
		thingsInSimulation.add(room1);
		thingsInSimulation.add(room2);
		thingsInSimulation.add(sonyMicrowave);
		thingsInSimulation.add(nest);
		thingsInSimulation.add(airConditioner);
		thingsInSimulation.add(comcastSec);
		thingsInSimulation.add(roomba);
	}
	
	public static Set<GUIThing> getThings() {
		return thingsInSimulation;
	}
}