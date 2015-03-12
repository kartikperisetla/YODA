package edu.cmu.sv.domain.smart_house.GUI;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GUI {
	static JFrame frame;
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new MainFrame("Smart Home");
				frame.setSize(500, 400);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}