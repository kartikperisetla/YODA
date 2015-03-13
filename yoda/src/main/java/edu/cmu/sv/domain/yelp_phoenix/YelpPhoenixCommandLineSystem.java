package edu.cmu.sv.domain.yelp_phoenix;

import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.DomainSpec;
import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonOntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonLexicon;
import edu.cmu.sv.yoda_environment.CommandLineYodaSystem;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YelpPhoenixCommandLineSystem extends CommandLineYodaSystem {
    static {
        // skeleton domain
        domainSpecs.add(new DomainSpec(
                "YODA skeleton domain",
                new YodaSkeletonLexicon(),
                new YodaSkeletonOntologyRegistry(),
                new NonDialogTaskRegistry(),
                new DatabaseRegistry()));
        // yelp phoenix domain
        domainSpecs.add(new DomainSpec(
                "Yelp Phoenix domain",
                new YelpPhoenixLexicon(),
                new YelpPhoenixOntologyRegistry(),
                new YelpPhoenixNonDialogTaskRegistry(),
                new YelpPhoenixDatabaseRegistry()));
    }
}
