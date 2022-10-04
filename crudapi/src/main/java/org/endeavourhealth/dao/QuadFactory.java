package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccessImpl;

import java.util.Optional;

public class QuadFactory {

    public static Quad getInstance(PersistenceAccessImpl persistenceAccess, Optional<String> graphLabel){
        return new QuadImpl(persistenceAccess, graphLabel);
    }
}
