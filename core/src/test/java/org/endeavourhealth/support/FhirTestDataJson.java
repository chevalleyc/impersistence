package org.endeavourhealth.support;


import java.io.InputStream;

public enum FhirTestDataJson {
    FHIR_OBSERVATION_SYSTOLIC_BLOOD_PRESSURE("fhir resource observation w/systolic", "fhir_resource_observation_systolic.json"),
    FHIR_RESOURCE_SAMPLE_1("fhir encounter with extensions", "fhir_encounter_resource_1.json"),
    FHIR_ALLERGY("fhir allergy with extensions", "fhir_allergy.json"),
    FHIR_CONDITION("fhir condition with extensions", "fhir_condition.json"),
    FHIR_EPISODE_OF_CARE("fhir episode of care", "fhir_episode_of_care.json"),
    FHIR_LOCATION("fhir location", "fhir_location.json"),
    FHIR_ORGANIZATION("fhir organization", "fhir_organization.json"),
    FHIR_PATIENT("fhir patient", "fhir_patient.json"),
    FHIR_PRACTITIONER("fhir practitioner", "fhir_practitioner.json"),
    FHIR_ENCOUNTER("fhir encounter", "fhir_encounter.json"),
    FHIR_PROCEDURE_REQUEST("fhir procedure request", "fhir_procedure_request.json"),
    ;

    private final String filename;
    private final String description;

    FhirTestDataJson(String description, String filename) {
        this.filename = filename;
        this.description = description;
    }

    public InputStream getStream() {
        return getClass().getResourceAsStream("/sample_json/" + filename);
    }

    @Override
    public String toString() {
        return this.description;
    }
}

