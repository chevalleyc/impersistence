package org.endeavourhealth.dao;

import org.endeavourhealth.support.PersistenceAccess;
import org.endeavourhealth.tables.records.QuadRecord;
import org.endeavourhealth.visitor.ResourceFormat;
import org.endeavourhealth.visitor.ResourceVisitor;
import org.endeavourhealth.visitor.ResourceVisitorFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static org.endeavourhealth.Tables.QUAD;

public class Graph {

    private final PersistenceAccess persistenceAccess;
    private final String graphLabel;

    public Graph(PersistenceAccess persistenceAccess, String graphLabel) {
        this.persistenceAccess = persistenceAccess;
        this.graphLabel = graphLabel;
    }

    public Integer create(String resource, ResourceFormat resourceFormat){

        ResourceVisitor resourceVisitor = ResourceVisitorFactory.getInstance(resourceFormat).traverse(resource);

        try {
            //setup subject node
            Node subjectNode = NodeFactory.getInstance(persistenceAccess);
            subjectNode.setFromResource(resourceVisitor);
            UUID subjectNodeUUID = subjectNode.persist();

            //setup quad from resource
            Quad quad = QuadFactory.getInstance(persistenceAccess, graphLabel);

            return quad.persist(subjectNodeUUID, resourceVisitor);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Couldn't interpret resource in context:"+e);
        }

    }

    public UUID create(Node subject, Node predicate, Node object){

        try {
            //setup quad from resource
            Quad quad = QuadFactory.getInstance(persistenceAccess, graphLabel);

            return quad.persist(subject, predicate, object);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Couldn't interpret resource in context:"+e);
        }

    }

    public UUID create(UUID subjectId, UUID predicateId, UUID objectId){

        try {
            //setup quad from resource
            Quad quad = QuadFactory.getInstance(persistenceAccess, graphLabel);

            return quad.persist(subjectId, predicateId, objectId);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Couldn't interpret resource in context:"+e);
        }

    }

    public @Nullable QuadRecord retrieve(UUID tripleId){
            return persistenceAccess.context().fetchOne(QUAD, QUAD.ID.eq(tripleId));
    }

    public Integer delete(UUID id){
        return persistenceAccess.context().deleteFrom(QUAD).where(QUAD.ID.eq(id)).execute();
    }


    /**
     * don't create a triple (subject, predicate, person) as it is implicit in the node class representation
     * @param subjectId
     * @param personId
     * @return
     */
    public UUID createTriplePersonImplicitPredicate(UUID subjectId, UUID personId){

        try {
            //setup quad from resource
            Quad quad = QuadFactory.getInstance(persistenceAccess, graphLabel);

//            return quad.persist(subjectId, predicateId, objectId);
        }
        catch (Exception e){
            throw new IllegalArgumentException("Couldn't interpret resource in context:"+e);
        }

        return null;
    }

    /**
     * don't create a triple (subject, predicate, person) as it is implicit in the node class representation
     * @param subjectId
     * @param organisationId
     * @return
     */
    public UUID createTripleOrganisationImplicitPredicate(UUID subjectId, UUID organisationId){

        try {
            //setup quad from resource
            Quad quad = QuadFactory.getInstance(persistenceAccess, graphLabel);

//            return quad.persist(subjectId, predicateId, objectId);
            return null;
        }
        catch (Exception e){
            throw new IllegalArgumentException("Couldn't interpret resource in context:"+e);
        }

    }
}

