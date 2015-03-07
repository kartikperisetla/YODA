package edu.cmu.sv.database;

import edu.cmu.sv.yoda_environment.YodaEnvironment;

/**
 * Created by David Cohen on 3/7/15.
 */
public interface Sensor {

    /*
    * implement this method, which updates the YODA agent's database according to some new information from the world
    * */
    public void sense(YodaEnvironment targetEnvironment);


    /*
    * The time in milliseconds that should elapse between YODA calls to this sensor.
    * NOTE: this is a guideline for YODA, and no guarantee is made that it will be followed.
    * It *should* be <= the actual time difference between any two calls to sense()
    *
    * Sensing is actually done at the beginning of DST loop
    *
    * */
    public double sensingPeriod = 1000;
}
