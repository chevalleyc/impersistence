package org.endeavourhealth.visitor.properties;

import org.endeavourhealth.visitor.fhir.FhirArbitraryJsonImpl;

import java.util.ArrayList;
import java.util.List;

public class Code {

    private String name;
    private List<CodeItem> codeItemList = new ArrayList<>();

    private FhirArbitraryJsonImpl codeArbitraryJson;

    public Code(FhirArbitraryJsonImpl codeArbitraryJson) {
        this.codeArbitraryJson = codeArbitraryJson;
    }


}
