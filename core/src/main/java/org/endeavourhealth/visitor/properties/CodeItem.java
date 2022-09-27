package org.endeavourhealth.visitor.properties;

import org.endeavourhealth.visitor.fhir.FhirArbitraryJsonImpl;

public class CodeItem {

    private String code;
    private String system;
    private String display;

    private FhirArbitraryJsonImpl codeJson;

    public CodeItem(FhirArbitraryJsonImpl codeJson) {
        this.codeJson = codeJson;
    }

    public void extract(){
        code = (String) codeJson.propertyValue("code");
        system = (String) codeJson.propertyValue("system");
        display = (String) codeJson.propertyValue("display");
    }
}
