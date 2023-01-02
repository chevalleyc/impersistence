package org.endeavourhealth.support;

import org.endeavourhealth.setup.QuadStoreConfig;
import org.jooq.DSLContext;

import java.io.Serializable;

public class PersistenceAccessImpl implements PersistenceAccess, Serializable {

    private final QuadStoreConfig quadStoreConfig;

    public PersistenceAccessImpl(QuadStoreConfig quadStoreConfig) {
        this.quadStoreConfig = quadStoreConfig;
    }

    @Override
    public DSLContext context(){
        return quadStoreConfig.dslContext();
    }
}
