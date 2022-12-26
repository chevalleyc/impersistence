package org.endeavourhealth.visitor.fhir;

import java.time.LocalDate;

public class EffectiveDate {

    private final String effectiveDateAsString;

    public EffectiveDate(String effectiveDateAsString) {
        this.effectiveDateAsString = effectiveDateAsString;
    }

    public LocalDate parse(){
        String datePart = effectiveDateAsString;
        if (datePart.contains("T")){
            datePart = datePart.split("T")[0];
        }

        return LocalDate.parse(datePart);
    }
}
