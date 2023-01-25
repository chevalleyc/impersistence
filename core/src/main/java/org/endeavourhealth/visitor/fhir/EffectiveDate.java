package org.endeavourhealth.visitor.fhir;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

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

        //try different format
        try {
            return LocalDate.parse(datePart);
        } catch (DateTimeException e){
            try {
                return Instant.
                        ofEpochMilli(new SimpleDateFormat("yyyy-MM").parse(datePart).getTime()).
                        atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (ParseException e1) {
                try {
                    return Instant.
                            ofEpochMilli(new SimpleDateFormat("yyyy").parse(datePart).getTime()).
                            atZone(ZoneId.systemDefault()).toLocalDate();
                }
                catch (ParseException e2){
                    throw  new IllegalArgumentException("could not intepret partial date:"+datePart);
                }
            }
        }
    }
}
