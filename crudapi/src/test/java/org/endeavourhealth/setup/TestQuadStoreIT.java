package org.endeavourhealth.setup;

import org.endeavourhealth.support.TestPersistenceAccess;
import org.jooq.Result;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.endeavourhealth.Tables.NODE;


public class TestQuadStoreIT extends TestPersistenceAccess {

    @Test
    public void testDatasource() {
        Result<?> result = context().selectFrom(NODE).limit(1).fetch();
        assertThat(result.size()).isEqualTo(0);
    }

}