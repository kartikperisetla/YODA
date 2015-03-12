package edu.cmu.sv.domain.smart_house.GUI;

import java.util.*;

public class Simulator {
	private static Set<GUIThing> thingsInSimulation;
	
	static {
		thingsInSimulation = new HashSet<>();
		GUIRoom room1 = new GUIRoom("Kitchen");
		GUIRoom room2 = new GUIRoom("Living Room");
		GUIPerson jane = new GUIPerson("Jane", "F", room1);
		GUIPerson john = new GUIPerson("John", "M", room2);
		GUIMicrowave sonyMicrowave = new GUIMicrowave("Sony Microwave", room1, true);
		GUIMicrowave samsungMicrowave = new GUIMicrowave("Samsung Microwave", room1, false);
		GUIThermostat nest = new GUIThermostat("Nest", room2, true);
		GUISecuritySystem comcastSec = new GUISecuritySystem("Comcast Security System", room2, true);
		GUIAC panasonicAC = new GUIAC("Panasonic AC", room2, true);
		thingsInSimulation.add(jane);
		thingsInSimulation.add(john);
		thingsInSimulation.add(room1);
		thingsInSimulation.add(room2);
		thingsInSimulation.add(sonyMicrowave);
		thingsInSimulation.add(samsungMicrowave);
		thingsInSimulation.add(nest);
		thingsInSimulation.add(panasonicAC);
		thingsInSimulation.add(comcastSec);
	}
	
	public static Set<GUIThing> getThings() {
		return thingsInSimulation;
	}
}