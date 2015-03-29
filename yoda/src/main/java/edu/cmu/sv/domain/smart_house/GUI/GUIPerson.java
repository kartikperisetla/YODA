package edu.cmu.sv.domain.smart_house.GUI;

import java.util.List;
import java.util.ArrayList;

public class GUIPerson extends GUIThing {
	private String gender;
	
	public GUIPerson(String name, String gender, GUIRoom room, String correspondingURI) {
		super(name, room, correspondingURI);
		this.gender = gender;
	}
	
	@Override
	public List<String> provideDetails() {
		List<String> ans = new ArrayList<>();
		ans.add("Name: " + this.getName() + "\n");
		ans.add("Room: " + this.getRoom() + "\n");
		ans.add("Gender: " + this.gender);
		return ans;
	}
	
}