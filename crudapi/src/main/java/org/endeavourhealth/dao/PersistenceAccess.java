package org.endeavourhealth.dao;

import org.endeavourhealth.setup.QuadStore;
import org.jooq.DSLContext;

public class PersistenceAccess {

    private final QuadStore quadStore;

    public PersistenceAccess(QuadStore quadStore) {
        this.quadStore = quadStore;
    }

    public DSLContext context(){
        return quadStore.dslContext();
    }
}
