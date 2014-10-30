package edu.cmu.sv.natural_language_generation;


import edu.cmu.sv.natural_language_generation.Templates.DefiniteReferenceWithClass0;
import edu.cmu.sv.natural_language_generation.Templates.DefiniteReferenceWithClassAndRelation0;
import edu.cmu.sv.natural_language_generation.Templates.FragmentTemplate0;
import edu.cmu.sv.natural_language_generation.Templates.NamedEntityFromLabelTemplate0;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 10/27/14.
 */
public class GrammarRegistry {
    public static Set<Class<? extends Template>> grammar1_roots = new HashSet<>();
    public static Set<Class<? extends Template>> grammar1 = new HashSet<>();

    static {
        grammar1.add(NamedEntityFromLabelTemplate0.class);
        grammar1.add(DefiniteReferenceWithClass0.class);
        grammar1.add(DefiniteReferenceWithClassAndRelation0.class);
        grammar1_roots.add(FragmentTemplate0.class);
    }
}
