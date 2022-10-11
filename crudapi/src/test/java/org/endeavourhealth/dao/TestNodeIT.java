package org.endeavourhealth.dao;

import org.endeavourhealth.support.DummyNodes;
import org.endeavourhealth.support.TestPersistenceAccess;
import org.endeavourhealth.tables.records.NodeHistoryRecord;
import org.endeavourhealth.tables.records.NodeRecord;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.endeavourhealth.Tables.NODE_HISTORY;

public class TestNodeIT extends TestPersistenceAccess {

    @Test
    public void testStoreNode(){

        UUID nodeId = DummyNodes.arbitrary(this);

        assertThat(nodeId).isNotNull();

        //retrieve node with id
        NodeRecord retrieved = NodeFactory.retrieve(this, nodeId);

        assertThat(retrieved).isNotNull();

        Integer result = NodeFactory.delete(this, nodeId);

        assertThat(result).isEqualTo(1);

        //check node_history (manual at the moment)
        NodeHistoryRecord nodeHistoryRecord = this.context().fetchOne(NODE_HISTORY, NODE_HISTORY.ID.eq(nodeId));

        assertThat(nodeHistoryRecord).isNotNull();
        assertThat(nodeHistoryRecord.get(NODE_HISTORY.ID)).isEqualTo(nodeId);

//        assertThat(NodeFactory.retrieve(this, nodeId).isEmpty()).isTrue();

    }

    @Test
    public void testStoreNodePersonAndOrganisation(){

        Node node = NodeFactory.getInstance(this);
        node.setOrganisationId(DummyNodes.organisation(this));
        node.setPersonId(DummyNodes.person(this));
        node.setName("test node");
        node.setEffectiveDate(LocalDateTime.now());
        node.setType(1234);
        node.setProperties("{\"a\":\"b\"}");
        UUID nodeId = node.persist();

        assertThat(nodeId).isNotNull();

    }

    @Test
    public void testStoreInconsistentNode(){

        Node node = NodeFactory.getInstance(this);
        //these will trigger a referential integrity violation
        node.setOrganisationId(UUID.randomUUID());
        node.setPersonId(UUID.randomUUID());
        node.setName("test node");
        node.setEffectiveDate(LocalDateTime.now());
        node.setType(1234);
        node.setProperties("{\"a\":\"b\"}");
        try {
            node.persist();

            Assert.fail("didn't detect referential integrity violation");
        } catch (Exception e){
            //continue
        }

    }
}