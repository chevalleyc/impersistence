package org.endeavourhealth.support;

import org.jooq.DSLContext;

public interface PersistenceAccess {
    DSLContext context();
}
