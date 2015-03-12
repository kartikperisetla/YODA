package edu.cmu.sv.domain.smart_house.GUI;

public abstract class GUIElectronic extends GUIThing {
	private boolean state;
	
	public GUIElectronic(String name, GUIRoom room, boolean state) {
		super(name, room);
		this.state = state;
	}

	public void toggleSwitch() {
		state = state ? false : true;
		((MainFrame) GUI.frame).refreshGUI();
	}
	
	public boolean getState() {
		return state;
	}
}