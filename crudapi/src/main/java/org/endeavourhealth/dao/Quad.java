package org.endeavourhealth.dao;

import org.endeavourhealth.visitor.ResourceVisitor;

import java.util.UUID;

public interface Quad {

    Integer create(UUID subjectId, ResourceVisitor resourceVisitor);
}
