package edu.cmu.sv.domain.smart_house;

import edu.cmu.sv.domain.DatabaseRegistry;
import edu.cmu.sv.domain.DomainSpec;
import edu.cmu.sv.domain.NonDialogTaskRegistry;
import edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixDatabaseRegistry;
import edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixLexicon;
import edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixNonDialogTaskRegistry;
import edu.cmu.sv.domain.yelp_phoenix.YelpPhoenixOntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.YODASkeletonOntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonLexicon;
import edu.cmu.sv.yoda_environment.CommandLineYodaSystem;

/**
 * Created by David Cohen on 3/3/15.
 */
public class SmartHouseCommandLineSystem extends CommandLineYodaSystem {
    static {
        // skeleton domain
        domainSpecs.add(new DomainSpec(
                "YODA skeleton domain",
                new YodaSkeletonLexicon(),
                new YODASkeletonOntologyRegistry(),
                new NonDialogTaskRegistry(),
                new DatabaseRegistry()));
        // yelp phoenix domain
        domainSpecs.add(new DomainSpec(
                "Smart house domain",
                new YelpPhoenixLexicon(),
                new YelpPhoenixOntologyRegistry(),
                new YelpPhoenixNonDialogTaskRegistry(),
                new YelpPhoenixDatabaseRegistry()));
    }
}
