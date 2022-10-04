package org.endeavourhealth.dao;

import org.endeavourhealth.support.TestPersistenceAccess;
import org.endeavourhealth.tables.records.NodeHistoryRecord;
import org.jooq.DSLContext;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.endeavourhealth.Tables.NODE;
import static org.endeavourhealth.Tables.NODE_HISTORY;

public class TestNodeIT extends TestPersistenceAccess {

    @Test
    public void testStoreNode(){

        UUID nodeId = NodeFactory.getInstance(this)
                .setOrganizationId(UUID.randomUUID())
                .setPersonId(UUID.randomUUID())
                .setName("test node")
                .setEffectiveDate(LocalDateTime.now())
                .setType(1234)
                .setProperties("{\"a\":\"b\"}")
                .store();

        assertThat(nodeId).isNotNull();

        //retrieve node with id
        Node retrieved = NodeFactory.retrieve(this, nodeId);

        assertThat(retrieved).isNotNull();

        Integer result = NodeFactory.delete(this, nodeId);

        assertThat(result).isEqualTo(1);

        //check node_history (manual at the moment)
        NodeHistoryRecord nodeHistoryRecord = this.context().fetchOne(NODE_HISTORY, NODE_HISTORY.ID.eq(nodeId));

        assertThat(nodeHistoryRecord).isNotNull();
        assertThat(nodeHistoryRecord.get(NODE_HISTORY.ID)).isEqualTo(nodeId);

        assertThat(NodeFactory.retrieve(this, nodeId).isEmpty()).isTrue();

    }
}