package edu.cmu.sv.domain.smart_house.GUI;

import java.util.List;
import java.util.ArrayList;

public class GUIMicrowave extends GUIElectronic {
	
	public GUIMicrowave(String name, GUIRoom room, boolean state, String correspondingURI) {
		super(name, room, state, correspondingURI);
	}

	@Override
	public List<String> provideDetails() {
		List<String> res = new ArrayList<>();
		res.add("Name: " + this.getName() + "\n");
		res.add("Room: " + this.getPosition() + "\nCurrent State: ");
		if(getState())
			res.add("ON");
		else
			res.add("OFF");
		return res;
	}

}