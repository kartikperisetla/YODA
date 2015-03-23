package edu.cmu.sv.domain.smart_house.GUI;

import java.util.List;
import java.util.ArrayList;

public class GUIRoom extends GUIThing {
	private int dustLevel;

	public GUIRoom(String name, int position, String correspondingURI, int dustLevel) {
		super(name, position, correspondingURI);
        this.dustLevel = dustLevel;
	}

    public int getDustLevel() {
        return dustLevel;
    }

    public void setDustLevel(int dustLevel) {
        this.dustLevel = dustLevel;
    }

    @Override
	public List<String> provideDetails() {
		List<String> ans = new ArrayList<>();
		ans.add(this.getName() + " entities: \n");
		for(GUIThing o : Simulator.getThings()) {
			if(o.getPosition() == this.getPosition()) {  // check primitive comparison
				ans.add(o.getName() + "\n");
			}
		}
		return ans;
	}

}