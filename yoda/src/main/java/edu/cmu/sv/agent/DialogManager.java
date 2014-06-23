package edu.cmu.sv.agent;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by cohend on 6/22/14.
 */
public class DialogManager implements Runnable {
    ConcurrentLinkedQueue<String> inputQueue;

    public DialogManager(ConcurrentLinkedQueue<String> inputQueue) {
        this.inputQueue = inputQueue;
    }

    @Override
    public void run() {
        while (true){
            System.out.println("looping...");
            String inputString = inputQueue.poll();
            if (inputString != null) {
                System.out.println("dialog manager heard something!: " + inputString);
            }
            try {
                Thread.sleep(500); //
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
