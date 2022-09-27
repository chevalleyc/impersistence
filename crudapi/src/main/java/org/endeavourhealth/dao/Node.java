package org.endeavourhealth.dao;

import org.endeavourhealth.tables.records.NodeRecord;
import org.endeavourhealth.visitor.ResourceVisitor;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Node {
    Node setResourceId(UUID id);

    Node setPersonId(UUID id);

    Node setOrganizationId(UUID id);

    Node setIri(String iri);

    Node setName(String name);

    Node setName(String name, String terminologyId, String code);

    Node setEffectiveDate(LocalDateTime localDateTime);

    Node setType(Integer type);

    Node setProperties(String properties);

    Node setResourceType(String resourceType);

    Node setFromResource(ResourceVisitor resourceVisitor);

    UUID store();

    Integer delete(UUID id);

    //TODO: to be implemented
    Integer update(UUID id, NodeRecord nodeRecord);
}
