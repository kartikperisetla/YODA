package edu.cmu.sv.natural_language_generation.internal_templates;

import edu.cmu.sv.database.dialog_task.ReferenceResolution;
import edu.cmu.sv.natural_language_generation.Template;
import edu.cmu.sv.database.Ontology;
import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.misc.UnknownThingWithRoles;
import edu.cmu.sv.domain.yoda_skeleton.ontology.noun.Noun;
import edu.cmu.sv.domain.yoda_skeleton.ontology.role.HasName;
import edu.cmu.sv.semantics.SemanticsModel;
import edu.cmu.sv.utils.Assert;
import edu.cmu.sv.yoda_environment.YodaEnvironment;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Cohen on 10/29/14.
 */
public class ThingWithNameTemplate0 implements Template {
    @Override
    public Map<String, JSONObject> generateAll(JSONObject constraints, YodaEnvironment yodaEnvironment, int remainingDepth) {
        // required information to generate
        Class<? extends Thing> nounClass;
        // ensure that the constraints match this template
        try {
            Assert.verify(constraints.containsKey("class"));
            Assert.verify(constraints.containsKey(HasName.class.getSimpleName()));
//            System.out.println("ThingWithNameTemplate: "+constraints);
            Assert.verify(constraints.keySet().size() == 2);
            nounClass = Ontology.thingNameMap.get((String) constraints.get("class"));
            Assert.verify(Noun.class.isAssignableFrom(nounClass) || UnknownThingWithRoles.class.equals(nounClass));
        } catch (Assert.AssertException e){
            return new HashMap<>();
        }

//        System.out.println("ThingWithNameTemplate: here");

        // resolve reference, take top guess
        String entityURI = ReferenceResolution.resolveReference(yodaEnvironment, constraints, false).getTopHypothesis();

        // generate for the top resolution hypothesis
        JSONObject wrappedEntity = SemanticsModel.parseJSON(Ontology.webResourceWrap(entityURI));
        return yodaEnvironment.nlg.generateAll(wrappedEntity, yodaEnvironment, remainingDepth);
    }
}
