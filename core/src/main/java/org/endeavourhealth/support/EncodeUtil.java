package org.endeavourhealth.support;

import com.google.gson.GsonBuilder;

public class EncodeUtil {

    public static GsonBuilder gsonBuilderInstance() {
        GsonBuilder builder = new GsonBuilder();
        return builder;
    }

}
