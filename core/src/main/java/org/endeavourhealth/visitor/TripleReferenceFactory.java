package org.endeavourhealth.visitor;

import org.endeavourhealth.visitor.fhir.FhirReference;

public class TripleReferenceFactory {

    public static TripleReference getInstance(ResourceFormat resourceFormat, String referenceName, ArbitraryJson arbitraryJson){

        switch (resourceFormat){
            case DSTU2 -> {
                return new FhirReference(referenceName, arbitraryJson).extract();
            }
            default ->
                throw new IllegalStateException("Not implemented resource type:"+ resourceFormat);

        }

    }

}
