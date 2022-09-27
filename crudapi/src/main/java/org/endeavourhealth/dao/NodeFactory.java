package org.endeavourhealth.dao;

public class NodeFactory {

    public static Node getInstance(PersistenceAccess persistenceAccess){
        return new NodeImpl(persistenceAccess);
    }
}
