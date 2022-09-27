package org.endeavourhealth.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KebabStringTest {

    @Test
    public void testConvertUrlLast(){

        assertEquals("primarycareRecordedDateExtension", new KebabString("primarycare-recorded-date-extension").toCamel());
        assertEquals("primarycarerecordeddateextension", new KebabString("primarycarerecordeddateextension").toCamel());
        assertEquals("primary_care_recorded_date_extension", new KebabString("primary_care_recorded_date_extension").toCamel());
        assertEquals("", new KebabString("").toCamel());
        assertNull(new KebabString(null).toCamel());
    }

}