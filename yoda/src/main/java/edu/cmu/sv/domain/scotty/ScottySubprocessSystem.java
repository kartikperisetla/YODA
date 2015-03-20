package edu.cmu.sv.domain.scotty;

import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.DomainSpec;
import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonLexicon;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.yoda_environment.CommandLineYodaSystem;
import edu.cmu.sv.yoda_environment.SubprocessYodaSystem;

/**
 * Created by David Cohen on 3/4/15.
 */
public class ScottySubprocessSystem extends SubprocessYodaSystem {
    static {
        // skeleton domain
        domainSpecs.add(new DomainSpec(
                "YODA skeleton domain",
                new YodaSkeletonLexicon(),
                new YodaSkeletonOntologyRegistry(),
                new NonDialogTaskRegistry(),
                new DatabaseRegistry()));
        // scotty domain
        domainSpecs.add(new DomainSpec(
                "Scotty domain",
                new ScottyLexicon(),
                new ScottyOntologyRegistry(),
                new ScottyNonDialogTaskRegistry(),
                new ScottyDatabaseRegistry()));
    }
}


