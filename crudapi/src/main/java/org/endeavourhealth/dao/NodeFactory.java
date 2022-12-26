package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccess;
import org.endeavourhealth.tables.records.NodeRecord;

import java.util.UUID;

public class NodeFactory {

    private NodeFactory() {
    }

    public static Node getInstance(PersistenceAccess persistenceAccess){
        return new Node(persistenceAccess);
    }

    public static NodeRecord retrieve(PersistenceAccess persistenceAccess, UUID uuid){
        return new Node(persistenceAccess).retrieveRecord(uuid);
    }

    public static Integer delete(PersistenceAccess persistenceAccess, UUID uuid){
        return new Node(persistenceAccess).deleteRecord(uuid);
    }
}
