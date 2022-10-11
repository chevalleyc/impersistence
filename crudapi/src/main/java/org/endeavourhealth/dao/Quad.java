package org.endeavourhealth.dao;

import org.endeavourhealth.visitor.ResourceVisitor;

import java.util.UUID;

public interface Quad {

    Integer persist(UUID subjectId, ResourceVisitor resourceVisitor);

    UUID persist(Node subject, Node predicate, Node object);

    UUID persist(UUID subjectId, UUID predicateId, UUID objectId);


}
