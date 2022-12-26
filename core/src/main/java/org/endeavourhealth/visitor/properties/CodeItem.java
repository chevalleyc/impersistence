package org.endeavourhealth.visitor.properties;

import org.endeavourhealth.visitor.ArbitraryJson;

public class CodeItem {

    private String code;
    private String system;
    private String display;

    private ArbitraryJson codeJson;

    public CodeItem(ArbitraryJson codeJson) {
        this.codeJson = codeJson;
    }

    public CodeItem extract(){
        code = (String) codeJson.propertyValue("code");
        system = (String) codeJson.propertyValue("system");
        display = (String) codeJson.propertyValue("display");
        return this;
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
}
