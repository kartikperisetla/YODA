package edu.cmu.sv.utils;

/**
 * Created by David Cohen on 12/12/14.
 */
public class Assert {
    public static class AssertException extends Exception{}

    public static void verify(boolean x) throws AssertException {
        if (!x)
            throw new AssertException();
    }
}
