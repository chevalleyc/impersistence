package org.endeavourhealth.visitor.properties;

import org.apache.commons.io.IOUtils;
import org.endeavourhealth.support.FhirTestDataJson;
import org.endeavourhealth.visitor.ResourceFormat;
import org.endeavourhealth.visitor.TripleReferenceFactory;
import org.endeavourhealth.visitor.JsonHandlerFactory;
import org.endeavourhealth.visitor.fhir.FhirArbitraryJsonImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FhirReferenceTest {

    @Test
    public void testReferenceItem() throws IOException {
        ResourceFormat resourceFormat = ResourceFormat.valueOf("DSTU2");

        //get a sample resource
        String resource = IOUtils.toString(FhirTestDataJson.FHIR_OBSERVATION_SYSTOLIC_BLOOD_PRESSURE.getStream(), StandardCharsets.UTF_8);

        FhirArbitraryJsonImpl arbitraryJson = JsonHandlerFactory.getInstance(resourceFormat, resource).generate();

//        assertTrue(arbitraryJson.propertyStructure("subject").isReference());
        assertTrue(arbitraryJson.propertyStructure("performer").isReference());
//        assertFalse(arbitraryJson.propertyStructure("dummy").isReference());

        assertEquals("Patient", TripleReferenceFactory.getInstance(resourceFormat,"subject", arbitraryJson.propertyStructure("subject")).getReferencedItem(0));
        assertEquals(UUID.fromString("58b7cd45-cba6-4587-a1cf-957b39583988"), TripleReferenceFactory.getInstance(resourceFormat,"subject", arbitraryJson.propertyStructure("subject")).getReferencedUUID(0));
    }

}