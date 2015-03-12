package edu.cmu.sv.domain.smart_house.GUI;
import java.util.List;
import java.util.ArrayList;

public abstract class GUIThing {
	private String name;
	private GUIRoom room;
	
	public GUIThing(String name, GUIRoom room) {
		this.name = name;
		this.room = room;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract List<String> provideDetails();
	
	public GUIRoom getRoom() {
		return room;
	}
	
	@Override
	public String toString() {
		return name;
	}
}