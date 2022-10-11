package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccess;
import org.endeavourhealth.tables.records.NodeRecord;
import org.endeavourhealth.visitor.ResourceVisitor;
import org.jooq.Field;
import org.jooq.JSONB;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static org.endeavourhealth.Tables.NODE;


public class Node extends NodeRecord {

    private final PersistenceAccess persistenceAccess;

    public Node(PersistenceAccess persistenceAccess) {
        super();
        this.persistenceAccess = persistenceAccess;
    }

    public void setName(String name, String terminologyId, String code){
        CodedText codedText = new CodedText(name);
        codedText.setTerminologyProps(terminologyId, code);
        setName(Objects.requireNonNull(codedText).record());
    }

    public void setName(String name){
        CodedText codedText = new CodedText(name);
        setName(Objects.requireNonNull(codedText).record());
    }

    public String getNameValue(){
        return getName().getValue();
    }


    public void setEffectiveDate(LocalDateTime localDateTime){
        setEffectiveDate(Timestamp.valueOf(localDateTime));
    }

    public void setProperties(String fromJson){
        setProperties(JSONB.valueOf(fromJson));
    }


    public void setFromResource(ResourceVisitor resourceVisitor){
        setId(resourceVisitor.getResourceId());
        setName(resourceVisitor.name());
        setPersonId(resourceVisitor.getPersonRefId());
        setOrganisationId(resourceVisitor.getOrganizationRefId());
        if (resourceVisitor.effectiveDate().isPresent())
            setEffectiveDate(resourceVisitor.effectiveDate().get());
        //TODO: resolve or change DB data type
//        setType(resourceVisitor.getResourceType());
        setProperties(JSONB.valueOf(resourceVisitor.propertiesToJson()));
    }

    public boolean isEmpty(){
        return Objects.isNull(this.getId());
    }

    public UUID persist(){
        NodeRecord jdbcBoundRecord = persistenceAccess.context().newRecord(NODE);

        for (Field field: this.fields())
            jdbcBoundRecord.setValue(field, this.getValue(field.getName()));

        jdbcBoundRecord.setId(UUID.randomUUID());
        jdbcBoundRecord.setSysTransaction(Timestamp.valueOf(LocalDateTime.now()));
        jdbcBoundRecord.store();
        return jdbcBoundRecord.getId();
    }

    public Integer delete(UUID id){
        return persistenceAccess.context().deleteFrom(NODE).where(NODE.ID.eq(id)).execute();
    }

    public NodeRecord retrieve(UUID uuid){
        return persistenceAccess.context().fetchOne(NODE, NODE.ID.eq(uuid));
    }

    //TODO: to be implemented
    //update fields !
    public Integer update(UUID id, NodeRecord nodeRecord){
        throw new IllegalStateException("Not yet implemented");
    }
}
