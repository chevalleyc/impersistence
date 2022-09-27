package org.endeavourhealth.visitor;

import org.apache.commons.io.IOUtils;
import org.endeavourhealth.support.FhirTestDataJson;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ResourceVisitorTest {

    @Test
    public void testResourceVisitor() throws IOException {

        //get a sample resource
        String resource = IOUtils.toString(FhirTestDataJson.FHIR_CONDITION.getStream(), StandardCharsets.UTF_8);

        ResourceVisitor resourceVisitor = ResourceVisitorFactory.getInstance(ResourceFormat.DSTU2).traverse(resource);

        assertNotNull(resourceVisitor.propertiesToJson());

        assertEquals(3, resourceVisitor.referencesSize());
        assertEquals(7, resourceVisitor.propertiesSize());

        assertEquals(UUID.fromString("44157a69-dd14-47a6-96ae-a6fdcd37f6c2"), resourceVisitor.getResourceId());
        assertEquals(UUID.fromString("0ec97982-58a2-4065-b000-8cb13ae0cb3b"), resourceVisitor.getPersonRefId());
        assertThat(resourceVisitor.name()).isEqualToIgnoringCase("primarycareCondition");
        assertNull(resourceVisitor.getOrganizationRefId());

        List<UUID> refUUIDs = new ArrayList<>();
        //check for other references
        resourceVisitor.referenceIterator().forEachRemaining(tr -> tr.referenceIterator().forEachRemaining(il -> refUUIDs.add(il.getReferencedUUID())));

        assertThat(refUUIDs)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                        UUID.fromString("6fb10b79-7197-4663-b3fa-3b417442bd64"),
                        UUID.fromString("6d44dd44-f1bf-49ef-81b8-c333c4a30c64"),
                        UUID.fromString("6fb10b79-7197-4663-b3fa-3b417442bd64")
                );

    }

}