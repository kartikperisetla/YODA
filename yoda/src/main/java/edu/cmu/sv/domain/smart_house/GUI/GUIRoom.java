package edu.cmu.sv.domain.smart_house.GUI;

import java.util.List;
import java.util.ArrayList;

public class GUIRoom extends GUIThing {
	private int dustLevel;
	private double temperature;

	public GUIRoom(String name, String correspondingURI, int dustLevel, double temperature) {
		super(name, null, correspondingURI);
        this.dustLevel = dustLevel;
		this.temperature = temperature;
	}

    public int getDustLevel() {
        return dustLevel;
    }

    public void setDustLevel(int dustLevel) {
        this.dustLevel = dustLevel;
    }

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

    @Override
	public List<String> provideDetails() {
		List<String> ans = new ArrayList<>();
		ans.add(this.getName() + " entities: \n");
		ans.add("Temp: " + String.valueOf(temperature) + (char)186 + "F\n");
		for(GUIThing o : Simulator.getThings()) {
			if(!(o instanceof GUIRoom) && o.getRoom().equals(this.getRoom())) {
				ans.add(o.getName() + "\n");
			}
		}
		return ans;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		GUIRoom otherRoom = (GUIRoom)obj;
		return (this.getName() == otherRoom.getName() ||
				(this.getName() != null && this.getName().equals(otherRoom.getName()))) &&
				(this.getName() == otherRoom.getName() ||
				(this.getCorrespondingURI() != null && this.getCorrespondingURI().equals(otherRoom.getCorrespondingURI()))) &&
				this.dustLevel == otherRoom.dustLevel &&
				this.temperature == otherRoom.temperature;
	}

}