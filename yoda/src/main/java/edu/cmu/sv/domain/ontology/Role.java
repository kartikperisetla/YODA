package edu.cmu.sv.domain.ontology;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by David Cohen on 6/15/15.
 */
public class Role {
    public boolean isQualityRole;
    public boolean isInverseRole;
    public String name;
    public Set<Object> domain = new HashSet<>();
    public Set<Object> range = new HashSet<>();

    public Set<Object> getDomain() {
        return domain;
    }

    public Set<Object> getRange() {
        return range;
    }

    public Role(String name, boolean isQualityRole, boolean isInverseRole) {
        this.name = name;
        this.domain.addAll(domain);
        this.range.addAll(range);
        this.isQualityRole = isQualityRole;
        this.isInverseRole = isInverseRole;
    }
}
