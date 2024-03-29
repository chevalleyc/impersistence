package org.endeavourhealth.support;

import org.endeavourhealth.dao.Node;
import org.endeavourhealth.dao.NodeFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class DummyNodes {

    public static UUID organisation(PersistenceAccess persistenceAccess){

        Node organisationNode = NodeFactory.getInstance(persistenceAccess);
        organisationNode.setName("Organisation"+ new Random().nextInt());
        organisationNode.setIri("http://endhealth.info/im#Organisation");
        organisationNode.setEffectiveDate(LocalDate.now());
        organisationNode.setType("test");
        organisationNode.setProperties("{\"a\":\"b\"}");
        return organisationNode.persist();
    }

    public static UUID person(PersistenceAccess persistenceAccess){
        Node personNode = NodeFactory.getInstance(persistenceAccess);
        personNode.setName("Person"+new Random().nextInt());
        personNode.setIri("http://endhealth.info/im#Patient");
        personNode.setEffectiveDate(LocalDate.now());
        personNode.setType("test");
        personNode.setProperties("{\"a\":\"b\"}");
       return personNode.persist();

    }

    public static UUID arbitrary(PersistenceAccess persistenceAccess){
        Node node = NodeFactory.getInstance(persistenceAccess);
        node.setOrganisationId(DummyNodes.organisation(persistenceAccess));
        node.setPersonId(DummyNodes.person(persistenceAccess));
        node.setName("test node");
        node.setEffectiveDate(LocalDate.now());
        node.setType("test");
        node.setProperties("{\"a\":\"b\"}");
       return node.persist();
    }

    public static UUID arbitrary(PersistenceAccess persistenceAccess, UUID personId, UUID organisationId){
        Node node = NodeFactory.getInstance(persistenceAccess);
        node.setOrganisationId(organisationId);
        node.setPersonId(personId);
        node.setName("test node");
        node.setEffectiveDate(LocalDate.now());
        node.setType("test");
        node.setProperties("{\"a\":\"b\"}");
        return node.persist();
    }

    public static UUID arbitrary(PersistenceAccess persistenceAccess, String name, UUID personId, UUID organisationId){
        Node node = NodeFactory.getInstance(persistenceAccess);
        node.setOrganisationId(organisationId);
        node.setPersonId(personId);
        node.setName(name);
        node.setEffectiveDate(LocalDate.now());
        node.setType("test");
        node.setProperties("{\"a\":\"b\"}");
        return node.persist();
    }
}
