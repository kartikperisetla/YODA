package edu.cmu.sv.visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by cohend on 6/22/14.
 */
public class TextInputGUI {

    ConcurrentLinkedQueue<String> outputQueue;
    JFrame frame;
    JLabel label;
    JTextField textField;
    JButton button;

    public TextInputGUI(ConcurrentLinkedQueue<String> outputQueue) {
        this.outputQueue = outputQueue;
    }

    private void createAndShowGUI(){
        frame = new JFrame("Text Input GUI");
        label = new JLabel("Type a message to the dialog system:");
        frame.getContentPane().add(label, BorderLayout.NORTH);
        textField = new JTextField();
        frame.getContentPane().add(textField, BorderLayout.CENTER);
        button = new JButton("Submit");
        button.addActionListener(actionEvent -> {
            outputQueue.add(textField.getText());
            textField.setText("");
        });
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    public void go(){
        javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);
    }

}
