package org.endeavourhealth.visitor;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public interface ResourceVisitor {
    String propertiesToJson();

    Integer propertiesSize();

    UUID getResourceId();

    TripleReference getOrganizationRef();

    UUID getOrganizationRefId();

    TripleReference getPersonRef();

    String name();

    UUID getPersonRefId();

    Integer referencesSize();

    Iterator<TripleReference> referenceIterator();

    ResourceVisitor traverse(String resource);

    Optional<LocalDate> effectiveDate();

    String getResourceType();

    String getType();
}
