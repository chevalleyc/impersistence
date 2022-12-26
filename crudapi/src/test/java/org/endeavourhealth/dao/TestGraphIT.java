package org.endeavourhealth.dao;

import org.endeavourhealth.support.DummyNodes;
import org.endeavourhealth.support.TestPersistenceAccess;
import org.endeavourhealth.tables.records.QuadHistoryRecord;
import org.endeavourhealth.tables.records.QuadRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.endeavourhealth.Tables.QUAD_HISTORY;

public class TestGraphIT extends TestPersistenceAccess {

    Graph graph;

    @Test
    public void testCreateTriple(){
        graph = new Graph(this, null);

        UUID person = DummyNodes.person(this);
        UUID organisation = DummyNodes.organisation(this);

        //create an arbitrary node
        UUID subject = DummyNodes.arbitrary(this, "subject", person, organisation);

        UUID predicate = DummyNodes.arbitrary(this, "predicate", person, organisation);

        UUID object = DummyNodes.arbitrary(this, "object", person, organisation);

        //triple
        UUID triple = graph.create(subject, predicate, object);

        assertThat(triple).isNotNull();

        //retrieve this triple
        QuadRecord quadRecord = graph.retrieve(triple);

        //check data
        assertThat(new Node(this).retrieveRecord(quadRecord.getSubjectId()).getName().getValue()).isEqualTo("subject");
        assertThat(new Node(this).retrieveRecord(quadRecord.getPredicateId()).getName().getValue()).isEqualTo("predicate");
        assertThat(new Node(this).retrieveRecord(quadRecord.getObjectId()).getName().getValue()).isEqualTo("object");

        //delete
        assertThat(graph.delete(triple)).isEqualTo(1);

        //check history record
        QuadHistoryRecord quadHistoryRecord = this.context().fetchOne(QUAD_HISTORY, QUAD_HISTORY.ID.eq(triple));
        assertThat(quadHistoryRecord.getId()).isEqualTo(triple);
    }


    @Test
    public void testCreateInconsistentTriple(){
        graph = new Graph(this, null);

        try {

            graph.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

            Assert.fail("Should have raise a integrity violation exception!");
        }
        catch (Exception e){
            if (!(e instanceof IllegalArgumentException))
                Assert.fail(e.getMessage());

        }

    }

}