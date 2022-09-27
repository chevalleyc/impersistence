package org.endeavourhealth.dao;

import org.endeavourhealth.tables.records.GraphLabelRecord;

import java.util.UUID;

import static org.endeavourhealth.tables.GraphLabel.GRAPH_LABEL;

public class GraphLabel {

    private final PersistenceAccess persistenceAccess;

    public GraphLabel(PersistenceAccess persistenceAccess) {
        this.persistenceAccess = persistenceAccess;
    }

    public UUID getOrCreate(String label){
        if (!persistenceAccess.context().fetchExists(GRAPH_LABEL, GRAPH_LABEL.NAME.eq(label))){

            GraphLabelRecord graphLabelRecord = persistenceAccess.context().newRecord(GRAPH_LABEL);

            graphLabelRecord.setName(label);
            graphLabelRecord.store();
            return graphLabelRecord.getId();
        }
        else
            return persistenceAccess.context().fetchOne(GRAPH_LABEL, GRAPH_LABEL.NAME.eq(label)).getId();
    }
}
