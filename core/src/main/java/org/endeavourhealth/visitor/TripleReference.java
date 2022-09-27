package org.endeavourhealth.visitor;

import org.endeavourhealth.visitor.properties.ReferencedItem;

import java.util.Iterator;
import java.util.UUID;

public interface TripleReference {
    boolean isReference();

    boolean isExtensionReference();

    Iterator<ReferencedItem> referenceIterator();

    String getReferencedItem(int index);

    UUID getReferencedUUID(int index);

    String getReferenceName();
}
