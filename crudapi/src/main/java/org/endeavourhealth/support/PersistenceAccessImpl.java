package org.endeavourhealth.support;

import org.endeavourhealth.setup.QuadStoreConfig;
import org.jooq.DSLContext;

public class PersistenceAccessImpl implements PersistenceAccess {

    private final QuadStoreConfig quadStoreConfig;

    public PersistenceAccessImpl(QuadStoreConfig quadStoreConfig) {
        this.quadStoreConfig = quadStoreConfig;
    }

    @Override
    public DSLContext context(){
        return quadStoreConfig.dslContext();
    }
}
