package org.endeavourhealth.dao;

import org.endeavourhealth.tables.records.NodeRecord;
import org.endeavourhealth.visitor.ResourceVisitor;
import org.jooq.JSONB;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.endeavourhealth.Tables.NODE;


public class NodeImpl implements Node {

    private NodeRecord nodeRecord;
    private final PersistenceAccess persistenceAccess;

    public NodeImpl(PersistenceAccess persistenceAccess) {
        this.persistenceAccess = persistenceAccess;
        nodeRecord = persistenceAccess.context().newRecord(NODE);
    }

    @Override
    public Node setResourceId(UUID id){
        this.nodeRecord.setId(Objects.requireNonNull(id));
        return this;
    }

    @Override
    public Node setPersonId(UUID id){
        this.nodeRecord.setPersonId(id);
        return this;
    }

    @Override
    public Node setOrganizationId(UUID id){
        this.nodeRecord.setOrganisationId(id);
        return this;
    }

    @Override
    public Node setIri(String iri){
        this.nodeRecord.setIri(iri);
        return this;
    }

    @Override
    public Node setName(String name, String terminologyId, String code){
        CodedText codedText = new CodedText(name);
        codedText.setTerminologyProps(terminologyId, code);
        this.nodeRecord.setName(Objects.requireNonNull(codedText).record());
        return this;
    }
    @Override
    public Node setName(String name){
        CodedText codedText = new CodedText(name);
        this.nodeRecord.setName(Objects.requireNonNull(codedText).record());
        return this;
    }


    @Override
    public Node setEffectiveDate(LocalDateTime localDateTime){
        this.nodeRecord.setEffectiveDate(Timestamp.valueOf(localDateTime));
        return this;
    }

    @Override
    public Node setType(Integer type){
        this.nodeRecord.setType(Objects.requireNonNull(type));
        return this;
    }

    @Override
    public Node setProperties(String properties){
        this.nodeRecord.setProperties(JSONB.valueOf(properties));
        return this;
    }

    @Override
    public Node setResourceType(String resourceType){
//        this.nodeRecord.setResourceType(resourceType);
        return this;
    }

    @Override
    public Node setFromResource(ResourceVisitor resourceVisitor){
        setResourceId(resourceVisitor.getResourceId());
        setName(resourceVisitor.name());
        setPersonId(resourceVisitor.getPersonRefId());
        setOrganizationId(resourceVisitor.getOrganizationRefId());
        if (resourceVisitor.effectiveDate().isPresent())
            setEffectiveDate(resourceVisitor.effectiveDate().get());
        //TODO: resolve or change DB data type
//        setType(resourceVisitor.getResourceType());
        setProperties(resourceVisitor.propertiesToJson());
        return this;
    }

    @Override
    public UUID store(){
        nodeRecord.setCreatedTs(Timestamp.valueOf(LocalDateTime.now()));
        nodeRecord.store();
        return nodeRecord.getId();
    }

    @Override
    public Integer delete(UUID id){
        return persistenceAccess.context().deleteFrom(NODE).where(NODE.ID.eq(id)).execute();
    }

    //TODO: to be implemented
    //update fields !
    @Override
    public Integer update(UUID id, NodeRecord nodeRecord){
        throw new IllegalStateException("Not yet implemented");
    }
}
