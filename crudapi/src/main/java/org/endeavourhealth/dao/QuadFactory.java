package org.endeavourhealth.dao;

import java.util.Optional;

public class QuadFactory {

    public static Quad getInstance(PersistenceAccess persistenceAccess, Optional<String> graphLabel){
        return new QuadImpl(persistenceAccess, graphLabel);
    }
}
