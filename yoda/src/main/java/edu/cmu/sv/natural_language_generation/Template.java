package edu.cmu.sv.natural_language_generation;

import java.util.List;
import java.util.Set;

/**
 * Created by David Cohen on 10/27/14.
 *
 * A template corresponds to a CFG rule for generation
 *
 */
public interface Template {

    public Set<List<Object>> generateAll();

}
