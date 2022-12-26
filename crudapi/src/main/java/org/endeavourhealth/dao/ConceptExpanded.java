package org.endeavourhealth.dao;

import java.io.Serializable;
import java.util.List;

public class ConceptExpanded implements Serializable {
    private final String code; //f.e. isA in snomed: 116680003
    private final String system; //terminology
    private final String display; //name

    private List<String> expanded;

    public ConceptExpanded(String code, String system, String display) {
        this.code = code;
        this.system = system;
        this.display = display;
    }

    public void setExpanded(List<String> expanded){
        this.expanded = expanded;
    }

    public String getCode() {
        return code;
    }

    public String getSystem() {
        return system;
    }

    public String getDisplay() {
        return display;
    }

    public List<String> getExpanded() {
        return expanded;
    }
}
