package org.endeavourhealth.visitor;

import org.endeavourhealth.visitor.fhir.dstu2.Dstu2Json;

public class JsonHandlerFactory {

    public static ArbitraryJson getInstance(ResourceFormat version, String jsonOrigin){
        switch (version){
            case DSTU2: return new Dstu2Json(version, jsonOrigin);
            default:
                throw new IllegalArgumentException("unsupported version");
        }
    }

    public static ArbitraryJson getInstance(ResourceFormat version, Object pojoJson){
        switch (version){
            case DSTU2: return new Dstu2Json(version, pojoJson);
            default:
                throw new IllegalArgumentException("unsupported version");
        }
    }

}
