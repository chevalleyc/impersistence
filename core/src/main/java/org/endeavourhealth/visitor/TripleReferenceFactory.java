package org.endeavourhealth.visitor;

import org.endeavourhealth.visitor.fhir.FhirReference;

public class TripleReferenceFactory {

    private TripleReferenceFactory() {
    }

    public static TripleReference getInstance(ResourceFormat resourceFormat, String referenceName, ArbitraryJson arbitraryJson){

        switch (resourceFormat){
            case DSTU2 -> {
                TripleReference tripleReference = new FhirReference(referenceName, arbitraryJson).extract();
                return tripleReference;
            }
            default ->
                throw new IllegalStateException("Not implemented resource type:"+ resourceFormat);

        }

    }

}
