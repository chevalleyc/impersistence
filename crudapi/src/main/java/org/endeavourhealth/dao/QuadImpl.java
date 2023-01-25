package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccess;
import org.endeavourhealth.tables.records.QuadRecord;
import org.endeavourhealth.visitor.ResourceVisitor;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.endeavourhealth.Tables.NODE;
import static org.endeavourhealth.Tables.QUAD;

public class QuadImpl implements Quad{

    private final PersistenceAccess persistenceAccess;
    private final String graphLabel;

    public QuadImpl(PersistenceAccess persistenceAccess, String graphLabel) {
        this.persistenceAccess = persistenceAccess;
        this.graphLabel = graphLabel;
    }

    @Override
    public Integer persist(UUID subjectId, ResourceVisitor resourceVisitor) {

        AtomicReference<Integer> recordCreated = new AtomicReference<>(0);

        resourceVisitor.referenceIterator().forEachRemaining(
                ref -> ref.referenceIterator().forEachRemaining(tr -> {
                            String name = tr.getName();
                            UUID objectUuid = tr.getReferencedUUID();
                            QuadRecord quadRecord = persistenceAccess.context().newRecord(QUAD);

                            //check if object exists
                            if (!persistenceAccess.context().fetchExists(NODE, NODE.ID.eq(objectUuid))) {
                                throw new IllegalStateException("Object is non existent with id:" + objectUuid);
                            }

                            //check if this graphLabel already exists otherwise create a new one
                            UUID graphLabelId = null;

                            if (Objects.nonNull(graphLabel))
                                graphLabelId = new GraphLabel(persistenceAccess).getOrCreate(graphLabel);

                            //create predicate node
                            quadRecord.setSubjectId(resourceVisitor.getResourceId());
                            if (Objects.nonNull(graphLabelId))
                                quadRecord.setLabelId(graphLabelId);

                            //create predicate node
                            Node node = NodeFactory.getInstance(persistenceAccess);
                            node.setName(name);
                            node.setPersonId(resourceVisitor.getPersonRefId());
                            node.setOrganisationId(resourceVisitor.getOrganizationRefId());
                            UUID predicateNodeId = node.persist();

                            //store the corresponding quad
                            quadRecord.setSubjectId(subjectId);
                            quadRecord.setPredicateId(predicateNodeId);
                            quadRecord.setLabelId(graphLabelId);
                            quadRecord.setObjectId(objectUuid);
                            quadRecord.store();

                            recordCreated.updateAndGet(v -> v + 1);

                        }
                )
        );

        return recordCreated.get();
    }

    @Override
    public UUID persist(Node subject, Node predicate, Node object) {
        return persist(subject.getId(), predicate.getId(), predicate.getNameValue(), object.getId());
    }

    @Override
    public UUID persist(UUID subjectId, UUID predicateId, String predicateName, UUID objectId) {
        QuadRecord quadRecord = persistenceAccess.context().newRecord(QUAD);
        if (Objects.nonNull(graphLabel))
            quadRecord.setLabelId(new GraphLabel(persistenceAccess).getOrCreate(graphLabel));
        quadRecord.setSubjectId(subjectId);
        quadRecord.setPredicateId(predicateId);
        quadRecord.setPredicateName(predicateName);
        quadRecord.setObjectId(objectId);
        quadRecord.store();
        return quadRecord.getId();
    }

}
