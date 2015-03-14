package edu.cmu.sv.domain.smart_house.GUI;

import java.util.List;
import java.util.ArrayList;

public class GUIRoom extends GUIThing {
	
	public GUIRoom(String name, String correspondingURI) {
		super(name, null, correspondingURI);
	}

	@Override
	public List<String> provideDetails() {
		List<String> ans = new ArrayList<>();
		ans.add(this.getName() + " entities: \n");
		for(GUIThing o : Simulator.getThings()) {
			if(o.getRoom() == this) {  // check primitive comparison
				ans.add(o.getName() + "\n");
			}
		}
		return ans;
	}

}