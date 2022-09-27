package org.endeavourhealth.visitor.properties;

import java.util.UUID;

public class ReferencedItem {

    private String referencedItem;
    private UUID referencedUUID;
    private String name;

    public ReferencedItem(String name, String referencedItem, UUID referencedUUID) {
        this.name = name;
        this.referencedItem = referencedItem;
        this.referencedUUID = referencedUUID;
    }

    public String getReferencedItem() {
        return referencedItem;
    }

    public UUID getReferencedUUID() {
        return referencedUUID;
    }

    public String getName() {
        return name;
    }
}
