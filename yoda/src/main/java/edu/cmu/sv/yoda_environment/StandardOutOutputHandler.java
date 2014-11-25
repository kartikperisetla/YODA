package edu.cmu.sv.yoda_environment;

/**
 * Created by David Cohen on 11/25/14.
 */
public class StandardOutOutputHandler implements OutputHandler {
    @Override
    public void sendOutput(String nlgResult) {
        System.out.println(nlgResult);
    }
}
