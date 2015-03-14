package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.DomainSpec;
import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.smart_house.GUI.MainFrame;
import edu.cmu.sv.domain.yoda_skeleton.YODASkeletonOntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonLexicon;
import edu.cmu.sv.yoda_environment.CommandLineYodaSystem;

import javax.swing.*;

/**
 * Created by David Cohen on 3/4/15.
 */
public class SmartHouseCommandLineSystem extends CommandLineYodaSystem {
    public static JFrame frame;
    static {
        // skeleton domain
        domainSpecs.add(new DomainSpec(
                "YODA skeleton domain",
                new YodaSkeletonLexicon(),
                new YODASkeletonOntologyRegistry(),
                new NonDialogTaskRegistry(),
                new DatabaseRegistry()));
        // yelp phoenix domain
        domainSpecs.add(new DomainSpec(
                "Smart house domain",
                new SmartHouseLexicon(),
                new SmartHouseOntologyRegistry(),
                new SmartHouseNonDialogTaskRegistry(),
                new SmartHouseDatabaseRegistry()));

        // TODO: add GUI to the simultaneous launch runnable list
        //        simultaneousLaunch.add(new simulatorGUI());
        Runnable guiThread = new Runnable() {
            public void run() {
                frame = new MainFrame("Smart Home");
                frame.setSize(500, 400);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(guiThread);
    }
}


