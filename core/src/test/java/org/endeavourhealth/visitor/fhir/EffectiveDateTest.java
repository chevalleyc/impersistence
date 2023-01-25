package org.endeavourhealth.visitor.fhir;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EffectiveDateTest {

    @Test
    void testPartialDate(){

        LocalDate result;

        result = new EffectiveDate("2022-12-02T12:12").parse();
        assertEquals("2022-12-02", result.toString());

        result = new EffectiveDate("2022-12").parse();
        assertEquals("2022-12-01", result.toString());

        result = new EffectiveDate("2022").parse();
        assertEquals("2022-01-01", result.toString());

    }

}