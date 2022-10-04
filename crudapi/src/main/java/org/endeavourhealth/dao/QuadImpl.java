package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccess;
import org.endeavourhealth.support.PersistenceAccessImpl;
import org.endeavourhealth.tables.records.QuadRecord;
import org.endeavourhealth.visitor.ResourceVisitor;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.endeavourhealth.Tables.NODE;
import static org.endeavourhealth.Tables.QUAD;

public class QuadImpl implements Quad{

    private final PersistenceAccess persistenceAccess;
    private final Optional<String> graphLabel;

    public QuadImpl(PersistenceAccessImpl persistenceAccess, Optional<String> graphLabel) {
        this.persistenceAccess = persistenceAccess;
        this.graphLabel = graphLabel;
    }

    @Override
    public Integer create(UUID subjectId, ResourceVisitor resourceVisitor) {

        AtomicReference<Integer> recordCreated = new AtomicReference<>(0);

        resourceVisitor.referenceIterator().forEachRemaining(
                ref -> ref.referenceIterator().forEachRemaining(tr -> {
                            try {
                                String name = tr.getName();
                                UUID objectUuid = tr.getReferencedUUID();
                                String predicateName = tr.getReferencedItem();
                                QuadRecord quadRecord = persistenceAccess.context().newRecord(QUAD);

                                //check if object exists
                                if (!persistenceAccess.context().fetchExists(NODE, NODE.ID.eq(objectUuid))) {
                                    throw new IllegalStateException("Object is non existent with id:" + objectUuid);
                                }

                                //check if this graphLabel already exists otherwise create a new one
                                UUID graphLabelId = null;

                                if (graphLabel.isPresent())
                                    graphLabelId = new GraphLabel(persistenceAccess).getOrCreate(graphLabel.get());

                                //create predicate node
                                quadRecord.setSubjectId(resourceVisitor.getResourceId());
                                if (Objects.nonNull(graphLabelId))
                                    quadRecord.setLabelId(graphLabelId);

                                //create predicate node
                                UUID predicateNodeId =
                                        NodeFactory.getInstance(persistenceAccess)
                                                .setName(name)
                                                .setPersonId(resourceVisitor.getPersonRefId())
                                                .setOrganizationId(resourceVisitor.getOrganizationRefId())
                                                .store();

                                //store the corresponding quad
                                quadRecord.setSubjectId(subjectId);
                                quadRecord.setPredicateId(predicateNodeId);
                                quadRecord.setLabelId(graphLabelId);
                                quadRecord.setObjectId(objectUuid);
                                quadRecord.store();

                                recordCreated.updateAndGet(v -> v + 1);

                            } catch (RuntimeException e){
                                //normally, performs a rollback...
                                throw e;
                            }
                        }
                )
        );

        return recordCreated.get();
    }

}
