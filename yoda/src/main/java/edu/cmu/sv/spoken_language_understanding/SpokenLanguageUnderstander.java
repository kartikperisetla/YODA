package edu.cmu.sv.spoken_language_understanding;

import edu.cmu.sv.utils.StringDistribution;
import edu.cmu.sv.yoda_environment.YodaEnvironment;

/**
 * Created by David Cohen on 11/21/14.
 */
public interface SpokenLanguageUnderstander {
    void process1BestAsr(String asrResult);
    void processNBestAsr(StringDistribution asrNBestResult);
}
