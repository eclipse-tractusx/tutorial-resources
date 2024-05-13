/********************************************************************************
 *  Copyright (c) 2024 SAP SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       SAP SE - initial API and implementation
 *
 ********************************************************************************/

package org.eclipse.tractusx.mxd.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;

public class Converter {

    public static String toJson(Object object, ObjectMapper objectMapper) {
        if (object == null) {
            return null;
        }
        try {
            return object instanceof String ? object.toString() : objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EdcPersistenceException(e);
        }
    }
}
