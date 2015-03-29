package edu.cmu.sv.domain.smart_house.GUI;

import java.util.ArrayList;
import java.util.List;

public class GUIRoomba extends GUIElectronic {

	public GUIRoomba(String name, GUIRoom room, String correspondingURI) {
		super(name, room, true, correspondingURI);
	}
	
	@Override
	public List<String> provideDetails() {
		List<String> ans = new ArrayList<>();
		ans.add("Name: " + this.getName() + "\n");
		ans.add("Room: " + this.getRoom() + "\n");
        if(getState())
            ans.add("ON");
        else
            ans.add("OFF");
		return ans;
	}
	
}