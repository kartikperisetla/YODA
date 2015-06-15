package edu.cmu.sv.domain.yoda_skeleton.ontology.ontology2;

import edu.cmu.sv.domain.yoda_skeleton.ontology.Thing;
import edu.cmu.sv.domain.yoda_skeleton.ontology.ThingWithRoles;

import java.util.Set;

/**
 * Created by David Cohen on 6/15/15.
 */
public class Role2 {
    public boolean isQualityRole;
    public String name;
    public Set<Class <? extends ThingWithRoles>> domain;
    public Set<Class <? extends Thing>> range;

    public Set<Class<? extends ThingWithRoles>> getDomain() {
        return domain;
    }

    public void setDomain(Set<Class<? extends ThingWithRoles>> domain) {
        this.domain = domain;
    }

    public Set<Class<? extends Thing>> getRange() {
        return range;
    }

    public void setRange(Set<Class<? extends Thing>> range) {
        this.range = range;
    }

    public Role2(String name, Set<Class<? extends ThingWithRoles>> domain, Set<Class<? extends Thing>> range, boolean isQualityRole) {
        this.name = name;
        this.domain = domain;
        this.range = range;
        this.isQualityRole = isQualityRole;
    }
}
