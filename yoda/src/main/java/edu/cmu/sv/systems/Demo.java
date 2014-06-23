package edu.cmu.sv.systems;

import edu.cmu.sv.agent.DialogManager;
import edu.cmu.sv.visualization.TextInputGUI;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by cohend on 6/22/14.
 */
public class Demo {
    public static void main(String[] args) {

        ConcurrentLinkedQueue<String> dmInputQueue = new ConcurrentLinkedQueue<String>();

        TextInputGUI vis = new TextInputGUI(dmInputQueue);
        vis.go();

        System.out.println("creating a dialog manager");
        Thread dmThread = new Thread(new DialogManager(dmInputQueue));
        dmThread.start();
        try {
            dmThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        System.out.println("creating a database");
//        Database database = new Database();
//        database.doSomething();
    }
}
