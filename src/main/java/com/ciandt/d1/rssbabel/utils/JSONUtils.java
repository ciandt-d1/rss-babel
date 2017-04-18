package com.ciandt.d1.rssbabel.utils;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;

public class JSONUtils {

    private static ObjectMapper objectMapper;

    /**
     * Return the object mapper
     */
    public static ObjectMapper getObjectMapper() {

        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.getFactory()
                    .configure(com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
                    .configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
            objectMapper.registerModule(new JodaModule());
            objectMapper.registerModule(new AfterburnerModule());
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            return objectMapper;
        }

        return objectMapper;
    }
}