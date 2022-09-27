package org.endeavourhealth.support;

import java.util.regex.Pattern;

public class KebabString {

    String kebabString;

    public KebabString(String kebabString) {
        this.kebabString = kebabString;
    }

    public String toCamel(){
        if (kebabString == null)
            return null;
        return Pattern.compile("-(.)")
                .matcher(kebabString)
                .replaceAll(mr -> mr.group(1).toUpperCase());

    }
}
