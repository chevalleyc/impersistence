package org.endeavourhealth.visitor.properties;

import org.apache.commons.io.IOUtils;
import org.endeavourhealth.support.FhirTestDataJson;
import org.endeavourhealth.visitor.ResourceFormat;
import org.endeavourhealth.visitor.ArbitraryJson;
import org.endeavourhealth.visitor.JsonHandlerFactory;
import org.endeavourhealth.visitor.fhir.Extension;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtensionTest {

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("fhir_identifiers");
    private static ResourceFormat fhirVersion = ResourceFormat.DSTU2;

    @Test
    public void testExtensionReferences() throws IOException {

        //get a sample resource
        String resource = IOUtils.toString(FhirTestDataJson.FHIR_ALLERGY.getStream(), StandardCharsets.UTF_8);

        ArbitraryJson arbitraryJson = JsonHandlerFactory.getInstance(fhirVersion, resource).generate();

        //get the reference part
        ArbitraryJson extensionPart = arbitraryJson.propertyStructure(resourceBundle.getString("EXTENSION_IDENTIFIER"));

        Extension extension = new Extension(fhirVersion, extensionPart).extract();

        assertEquals(2, extension.referenceListSize());

        assertEquals(0, extension.extensionJson().size());
    }


    @Test
    public void testExtensionReferences2() throws IOException {

        //get a sample resource
        String resource = IOUtils.toString(FhirTestDataJson.FHIR_CONDITION.getStream(), StandardCharsets.UTF_8);

        ArbitraryJson arbitraryJson = JsonHandlerFactory.getInstance(fhirVersion, resource).generate();

        //get the reference part
        ArbitraryJson extensionPart = arbitraryJson.propertyStructure(resourceBundle.getString("EXTENSION_IDENTIFIER"));

        Extension extension = new Extension(fhirVersion, extensionPart).extract();

        assertEquals(1, extension.referenceListSize());

        assertEquals(2, extension.extensionJson().size());
    }
}