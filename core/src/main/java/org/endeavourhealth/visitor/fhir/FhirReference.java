package org.endeavourhealth.visitor.fhir;

import org.endeavourhealth.visitor.ArbitraryJson;
import org.endeavourhealth.visitor.TripleReferenceImpl;

public class FhirReference extends TripleReferenceImpl {

    public FhirReference(String referenceName, ArbitraryJson referenceJson) {
        super();
        this.referenceJson = referenceJson;
        this.referenceName = referenceName;
    }
}
