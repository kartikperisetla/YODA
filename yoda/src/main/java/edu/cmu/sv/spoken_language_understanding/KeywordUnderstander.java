package edu.cmu.sv.spoken_language_understanding;

import edu.cmu.sv.yoda_environment.YodaEnvironment;

/**
 * Created by David Cohen on 11/21/14.
 *
 * A simple keyword-based SLU system for quick-n-dirty tests
 *
 */
public class KeywordUnderstander implements SpokenLanguageUnderstander{
    YodaEnvironment yodaEnvironment;
    public KeywordUnderstander(YodaEnvironment yodaEnvironment) {
        this.yodaEnvironment = yodaEnvironment;
    }
}
