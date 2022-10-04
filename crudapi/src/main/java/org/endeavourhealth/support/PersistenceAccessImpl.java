package org.endeavourhealth.support;

import org.endeavourhealth.setup.QuadStore;
import org.jooq.DSLContext;

public class PersistenceAccessImpl implements PersistenceAccess {

    private final QuadStore quadStore;

    public PersistenceAccessImpl(QuadStore quadStore) {
        this.quadStore = quadStore;
    }

    @Override
    public DSLContext context(){
        return quadStore.dslContext();
    }
}
