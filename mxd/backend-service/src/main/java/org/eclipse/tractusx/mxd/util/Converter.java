package org.eclipse.tractusx.mxd.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;

public class Converter {

    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return object instanceof String ? object.toString() : new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EdcPersistenceException(e);
        }
    }
}
