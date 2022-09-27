package org.endeavourhealth.visitor;

import org.endeavourhealth.visitor.fhir.FhirResourceVisitor;

public class ResourceVisitorFactory {

        public static ResourceVisitor getInstance(ResourceFormat type){

            switch (type) {
                case DSTU2 :
                    return new FhirResourceVisitor(type);
            }
            throw new IllegalArgumentException("Not yet implemented visitor type:"+ type);
        }

}
