package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccess;
import org.endeavourhealth.tables.records.ConceptHierarchyRecord;
import org.endeavourhealth.tables.records.ConceptHierarchyXrefRecord;
import org.endeavourhealth.tables.records.NodeRecord;
import org.endeavourhealth.visitor.ResourceVisitor;
import org.jooq.Field;
import org.jooq.JSONB;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.endeavourhealth.Tables.*;


public class Node extends NodeRecord {

    private final PersistenceAccess persistenceAccess;
    private ConceptExpanded conceptExpanded;

    public Node(PersistenceAccess persistenceAccess) {
        super();
        this.persistenceAccess = persistenceAccess;
    }

    public Node setName(String name, String terminologyId, String code){
        CodedText codedText = new CodedText(name);
        codedText.setTerminologyProps(terminologyId, code);
        setName(Objects.requireNonNull(codedText).record());
        return this;
    }

    public Node setName(String name){
        CodedText codedText = new CodedText(name);
        setName(Objects.requireNonNull(codedText).record());
        return this;
    }

    public String getNameValue(){
        return getName().getValue();
    }


    public void setEffectiveDate(LocalDate localDate){
        setEffectiveDate(Timestamp.valueOf(localDate.atTime(LocalTime.of(0,0))));
    }

    public Node setProperties(String fromJson){
        setProperties(JSONB.valueOf(fromJson));
        return this;
    }

    public Node setFromResource(ResourceVisitor resourceVisitor){
        setId(resourceVisitor.getResourceId());
        setType(resourceVisitor.getType());
        setName(resourceVisitor.name());
        setPersonId(resourceVisitor.getPersonRefId());
        setOrganisationId(resourceVisitor.getOrganizationRefId());
        resourceVisitor.effectiveDate().ifPresent(
                this::setEffectiveDate
        );

        setProperties(JSONB.valueOf(resourceVisitor.propertiesToJson()));
        return this;
    }

    public boolean isEmpty(){
        return Objects.isNull(this.getId());
    }

    public void setConceptExpanded(String code, String system, String display, List<String> exandedAs){
        conceptExpanded = new ConceptExpanded(code, system, display);
        conceptExpanded.setExpanded(exandedAs);
    }

    public Node setConceptExpanded(ConceptExpanded conceptExpanded){
        this.conceptExpanded = conceptExpanded;
        return this;
    }

    public UUID persist(){
        NodeRecord nodeRecord = persistenceAccess.context().newRecord(NODE);

        for (Field field: this.fields())
            nodeRecord.setValue(field, this.getValue(field.getName()));

        if (nodeRecord.getId() == null)
            nodeRecord.setId(UUID.randomUUID());

        nodeRecord.setSysTransaction(Timestamp.valueOf(LocalDateTime.now()));
        nodeRecord.store();

        if (conceptExpanded != null){
            ConceptHierarchyRecord conceptHierarchyRecord = persistenceAccess.context().newRecord(CONCEPT_HIERARCHY);
            conceptHierarchyRecord.setId(UUID.randomUUID());
            conceptHierarchyRecord.setHierarchy(conceptExpanded.getExpanded().toArray(new String[]{}));
            conceptHierarchyRecord.store();
            ConceptHierarchyXrefRecord conceptHierarchyXrefRecord = persistenceAccess.context().newRecord(CONCEPT_HIERARCHY_XREF);
            conceptHierarchyXrefRecord.setId(UUID.randomUUID());
            conceptHierarchyXrefRecord.setHierarchyRef(conceptHierarchyRecord.getId());
            conceptHierarchyXrefRecord.setCode(conceptExpanded.getCode());
            conceptHierarchyXrefRecord.setName(conceptExpanded.getDisplay());
            conceptHierarchyXrefRecord.setTerminology(conceptExpanded.getSystem());
            conceptHierarchyXrefRecord.setNodeRef(nodeRecord.getId());
            conceptHierarchyXrefRecord.store();
        }
        return nodeRecord.getId();
    }

    public Integer deleteRecord(UUID id){
        persistenceAccess.context().deleteFrom(CONCEPT_HIERARCHY_XREF).where(CONCEPT_HIERARCHY_XREF.NODE_REF.eq(id));
        return persistenceAccess.context().deleteFrom(NODE).where(NODE.ID.eq(id)).execute();
    }

    public NodeRecord retrieveRecord(UUID uuid){
        return persistenceAccess.context().fetchOne(NODE, NODE.ID.eq(uuid));
    }

    public Node retrieve(UUID uuid){
        NodeRecord nodeRecord = retrieveRecord(uuid);
       return fromNodeRecord(nodeRecord);
    }

    public Node fromNodeRecord(NodeRecord nodeRecord){
        Node node = new Node(persistenceAccess);
        node.setPersonId(nodeRecord.getPersonId());
        node.setProperties(nodeRecord.getProperties());
        node.setFeederAudit(nodeRecord.getFeederAudit());
        node.setType(nodeRecord.getType());
        node.setEffectiveDate(nodeRecord.getEffectiveDate());
        node.setName(nodeRecord.getName());
        node.setIri(nodeRecord.getIri());
        node.setOrganisationId(nodeRecord.getOrganisationId());
        node.setId(nodeRecord.getId());
        //retrieve matching concept if any
        ConceptHierarchyXrefRecord conceptHierarchyXrefRecord = persistenceAccess.context().fetchOne(CONCEPT_HIERARCHY_XREF, CONCEPT_HIERARCHY_XREF.NODE_REF.eq(nodeRecord.getId()));
        if (conceptHierarchyXrefRecord != null){
            //get the referenced expanded hierarchy
            ConceptHierarchyRecord conceptHierarchyRecord = persistenceAccess.context().fetchOne(CONCEPT_HIERARCHY, CONCEPT_HIERARCHY.ID.eq(conceptHierarchyXrefRecord.getHierarchyRef()));
            if (conceptHierarchyRecord != null){
                node.setConceptExpanded(
                        conceptHierarchyXrefRecord.getCode(),
                        conceptHierarchyXrefRecord.getTerminology(),
                        conceptHierarchyXrefRecord.getName(),
                        Arrays.stream(conceptHierarchyRecord.getHierarchy()).toList()
                );
            }
        }
        return node;
    }

    //TODO: to be implemented
    //update fields !
    public Integer update(UUID id, NodeRecord nodeRecord){
        throw new IllegalStateException("Not yet implemented");
    }

    public ConceptExpanded getConceptExpanded() {
        return conceptExpanded;
    }
}
