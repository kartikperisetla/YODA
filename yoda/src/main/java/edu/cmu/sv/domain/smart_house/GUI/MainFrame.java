package edu.cmu.sv.domain.smart_house.GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainFrame extends JFrame {
	private ItemsPanel itemsPanel;
	private DetailsPanel detailsPanel;
	JList<GUIThing> list;
	DefaultListModel<GUIThing> listSelectionModel = new DefaultListModel<>();
	JTextArea detailsTextArea;
	JButton button;
	JScrollPane scroller;
    GUIThing thingBeingDisplayed;
	
	public MainFrame(String title) {
		super(title);
		setLayout(new BorderLayout());

		itemsPanel = new ItemsPanel();
		detailsPanel = new DetailsPanel();
		detailsPanel.setLayout(new BorderLayout());
		detailsTextArea = new JTextArea();
		detailsTextArea.setEditable(false);
		detailsTextArea.setLineWrap(true);
		
		button = new JButton("Toggle");

//		for(GUIThing thing : Simulator.getThings()) {
//            listSelectionModel.addElement(thing);
//        }
        Simulator.getThings().stream().forEach((thing) -> listSelectionModel.addElement(thing));
		
		//Items list
		list = new JList<>(listSelectionModel);
		list.setVisibleRowCount(3);
		scroller = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		list.setFixedCellHeight(30);
		list.setFixedCellWidth(150);
		itemsPanel.add(list);
		itemsPanel.add(scroller);

		ListenForList lForList = new ListenForList();
		list.addListSelectionListener(lForList);

		ListenForButton lForButton = new ListenForButton();
		button.addActionListener(lForButton);
		itemsPanel.add(button);
		
		detailsPanel.add(detailsTextArea, BorderLayout.CENTER);
		
		Container pane = this.getContentPane();

		pane.add(itemsPanel, BorderLayout.WEST);
		pane.add(detailsPanel, BorderLayout.CENTER);
	}

	public void refreshGUI() {
		List<String> detailsInfo = thingBeingDisplayed.provideDetails();
		StringBuilder detailsBuffer = new StringBuilder();
        detailsInfo.stream().forEach((s) -> detailsBuffer.append(s));
//		for(String s : detailsInfo) {
//			detailsBuffer.append(s);
//		}
		detailsTextArea.setText(detailsBuffer.toString());
		if(thingBeingDisplayed instanceof GUIElectronic) {
			String btnDisplay = detailsInfo.get(2).toString().equals("ON") ? "Turn Off" : "Turn On";
			button.setText(btnDisplay);
		}
	}	
	
	private class ListenForButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == button) {
				if(thingBeingDisplayed instanceof GUIElectronic) {
					((GUIElectronic)thingBeingDisplayed).toggleSwitch();
				}
			}
		}
	}
	
	private class ListenForList implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(e.getSource() == list) {
				if(!(thingBeingDisplayed instanceof GUIElectronic)) {
					button.setVisible(false);
				} else {
					button.setVisible(true);
				}
				thingBeingDisplayed = list.getSelectedValue();
				refreshGUI();
			}
		}
	}

}