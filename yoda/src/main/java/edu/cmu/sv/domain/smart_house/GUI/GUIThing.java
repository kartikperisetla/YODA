package edu.cmu.sv.domain.smart_house.GUI;
import java.util.List;

public abstract class GUIThing {
	private String name;
	private int position;
    private String correspondingURI;

    public GUIThing(String name, int room, String correspondingURI) {
        this.name = name;
        this.position = room;
        this.correspondingURI = correspondingURI;
    }

    public String getCorrespondingURI() {
        return correspondingURI;
    }

    public String getName() {
		return name;
	}
	
	public abstract List<String> provideDetails();
	
	public int getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return name;
	}
}