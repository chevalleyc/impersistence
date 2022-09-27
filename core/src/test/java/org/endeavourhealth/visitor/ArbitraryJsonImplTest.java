package org.endeavourhealth.visitor;

import org.endeavourhealth.support.FhirTestDataJson;
import org.junit.jupiter.api.Test;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ArbitraryJsonImplTest {

    @Test
    public void testVisitorObservation() throws IOException {

        //get a sample resource
        String resource = IOUtils.toString(FhirTestDataJson.FHIR_OBSERVATION_SYSTOLIC_BLOOD_PRESSURE.getStream(), StandardCharsets.UTF_8);

        ArbitraryJson arbitraryJson = JsonHandlerFactory.getInstance(ResourceFormat.DSTU2, resource).generate();

        assertTrue(arbitraryJson.isMap());

        assertEquals(11, arbitraryJson.propertyNames().size());

        assertTrue(arbitraryJson.propertyStructure("extension").isList());

        assertEquals(2, arbitraryJson.propertyStructure("extension").size());

        //check for a name value in list
        AtomicReference<Boolean> found = new AtomicReference<>(false);
        ArbitraryJson arbitraryProp = arbitraryJson.propertyStructure("extension");
        arbitraryProp.valuesIterator().forEachRemaining(object -> {
            ArbitraryJson arbitrary = JsonHandlerFactory.getInstance(ResourceFormat.DSTU2, object);
            if (arbitrary.propertyNames().contains("valueDateTime")) {
                found.set(true);
            } else {
                found.set(false);
            }
        });
        assertTrue(found.get());

        assertEquals("bb56593e-cf26-4741-a0d7-ffc4744feef3", arbitraryJson.propertyValue("id"));

        arbitraryJson.deletePropertyStructure("subject");
        assertTrue(arbitraryJson.propertyStructure("subject").isNull());

        arbitraryJson.deletePropertyStructure("dummy");
        assertEquals(10, arbitraryJson.propertyNames().size());
    }
}