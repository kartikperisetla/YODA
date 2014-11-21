package edu.cmu.sv.yoda_environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by David Cohen on 11/21/14.
 *
 * Implement this interface to create a YODA dialog system
 *
 */
public class CommandLineYodaSystem {

    YodaEnvironment

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String s;
        while ((s = in.readLine()) != null && s.length() != 0)
            System.out.println(s);
        // An empty line or Ctrl-Z terminates the program
    }

}
