package edu.cmu.sv.yoda_environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by David Cohen on 11/21/14.
 *
 * Command line interface to yoda dialog system
 *
 */
public class CommandLineYodaSystem {

    public static void main(String[] args) throws IOException {
        YodaEnvironment yodaEnvironment = YodaEnvironment.dialogTestingEnvironment();
        Thread dstThread = new Thread(yodaEnvironment.dst);
        dstThread.start();
        Thread dmThread = new Thread(yodaEnvironment.dm);
        dmThread.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s;
        // An empty line or Ctrl-Z terminates the program
        while ((s = in.readLine()) != null)
            if (s.length() !=0)
                yodaEnvironment.slu.process1BestAsr(s);
    }

}
