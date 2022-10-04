package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccessImpl;
import org.endeavourhealth.visitor.ResourceFormat;
import org.endeavourhealth.visitor.ResourceVisitor;
import org.endeavourhealth.visitor.ResourceVisitorFactory;

import java.util.Optional;
import java.util.UUID;

public class Graph {

    private final PersistenceAccessImpl persistenceAccess;
    private final Optional<String> graphLabel;

    public Graph(PersistenceAccessImpl persistenceAccess, Optional<String> graphLabel) {
        this.persistenceAccess = persistenceAccess;
        this.graphLabel = graphLabel;
    }

    public void createTriple(String resource, ResourceFormat resourceFormat){

        ResourceVisitor resourceVisitor = ResourceVisitorFactory.getInstance(resourceFormat).traverse(resource);

        try {
            //setup subject node
            Node subjectNode = NodeFactory.getInstance(persistenceAccess);
            subjectNode.setFromResource(resourceVisitor);
            UUID subjectNodeUUID = subjectNode.store();

            //setup quad from resource
            Quad quad = QuadFactory.getInstance(persistenceAccess, graphLabel);

            quad.create(subjectNodeUUID, resourceVisitor);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Couldn't interpret resource in context:"+e);
        }

    }
}
