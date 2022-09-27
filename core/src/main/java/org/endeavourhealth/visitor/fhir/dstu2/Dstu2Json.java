package org.endeavourhealth.visitor.fhir.dstu2;

import org.endeavourhealth.visitor.ResourceFormat;
import org.endeavourhealth.visitor.fhir.FhirArbitraryJsonImpl;

public class Dstu2Json extends FhirArbitraryJsonImpl {

    public Dstu2Json(ResourceFormat version, String jsonAsString) {
        super(version, jsonAsString);
    }

    public Dstu2Json(ResourceFormat version, Object aPojoJson) {
        super(version, aPojoJson);
    }
}
