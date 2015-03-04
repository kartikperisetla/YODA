package edu.cmu.sv.domain.yelp_phoenix;

import edu.cmu.sv.database.DatabaseRegistry;
import edu.cmu.sv.dialog_management.DialogRegistry;
import edu.cmu.sv.domain.DomainSpec;
import edu.cmu.sv.domain.yoda_skeleton.YODASkeletonOntologyRegistry;
import edu.cmu.sv.domain.yoda_skeleton.YodaSkeletonLexicon;
import edu.cmu.sv.yoda_environment.CommandLineYodaSystem;

/**
 * Created by David Cohen on 3/3/15.
 */
public class YelpPhoenixCommandLineSystem extends CommandLineYodaSystem {
    static {
        // skeleton domain
        domainSpecs.add(new DomainSpec(
                new YodaSkeletonLexicon(),
                new YODASkeletonOntologyRegistry(),
                new DialogRegistry(),
                new DatabaseRegistry()));
        // yelp phoenix domain
        domainSpecs.add(new DomainSpec(
                new PhoenixYelpLexicon(),
                new YelpPhoenixOntologyRegistry(),
                new DialogRegistry(),
                new DatabaseRegistry()));
    }
}
