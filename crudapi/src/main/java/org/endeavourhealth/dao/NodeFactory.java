package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccess;
import org.endeavourhealth.support.PersistenceAccessImpl;

import java.util.UUID;

public class NodeFactory {

    public static Node getInstance(PersistenceAccess persistenceAccess){
        return new NodeImpl(persistenceAccess);
    }

    public static Node retrieve(PersistenceAccess persistenceAccess, UUID uuid){
        return new NodeImpl(persistenceAccess).retrieve(uuid);
    }

    public static Integer delete(PersistenceAccess persistenceAccess, UUID uuid){
        return new NodeImpl(persistenceAccess).delete(uuid);
    }
}
