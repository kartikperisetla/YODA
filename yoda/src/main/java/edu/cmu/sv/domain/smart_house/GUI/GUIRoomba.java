package edu.cmu.sv.domain.smart_house.GUI;

import java.util.ArrayList;
import java.util.List;

public class GUIRoomba extends GUIThing {

	public GUIRoomba(String name, GUIRoom room, String correspondingURI) {
		super(name, room.getPosition(), correspondingURI);
	}
	
	@Override
	public List<String> provideDetails() {
		List<String> ans = new ArrayList<>();
		ans.add("Name: " + this.getName() + "\n");
		ans.add("Room: " + this.getPosition() + "\n");
		return ans;
	}
	
}