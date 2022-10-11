package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccess;
import org.endeavourhealth.support.PersistenceAccessImpl;

import java.util.Optional;

public class QuadFactory {

    public static Quad getInstance(PersistenceAccess persistenceAccess, String graphLabel){
        return new QuadImpl(persistenceAccess, graphLabel);
    }
}
