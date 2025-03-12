/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.tractusx.mxd.dataservice.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.web.spi.exception.ObjectConflictException;
import org.eclipse.edc.web.spi.exception.ObjectNotFoundException;
import org.eclipse.tractusx.mxd.dataservice.model.DataRecord;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1/data")
public class DataServiceApiController implements DataServiceApi {

    private final ConcurrentHashMap<String, DataRecord> database;
    private final ObjectMapper objectMapper;

    public DataServiceApiController(ConcurrentHashMap<String, DataRecord> database, ObjectMapper objectMapper) {
        this.database = database;
        this.objectMapper = objectMapper;
    }

    @GET
    @Override
    public Collection<DataRecord> getAll() {
        return database.values();
    }

    @GET
    @Path("/{id}")
    @Override
    public DataRecord findById(@PathParam("id") String id) {
        if(database.containsKey(id)) {
            return database.get(id);
        }
        throw new ObjectNotFoundException(DataRecord.class, id);
    }

    @POST
    @Override
    public String create(DataRecord dataRecord) {
        if (database.containsKey(dataRecord.id())) {
            throw new ObjectConflictException("DataRecord with id " + dataRecord.id() + " already exists");
        }
        database.put(dataRecord.id(), dataRecord);
        return createJsonResponse(dataRecord.id());
    }

    @PUT
    @Override
    public void update(DataRecord dataRecord) {
        if (!database.containsKey(dataRecord.id())) {
            throw new ObjectNotFoundException(DataRecord.class, dataRecord.id());
        }
        database.put(dataRecord.id(), dataRecord);
    }

    @DELETE
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") String id) {
        if (!database.containsKey(id)) {
            throw new ObjectNotFoundException(DataRecord.class, id);
        }
        database.remove(id);
    }

    private String createJsonResponse(String id) {
        JsonNode jsonResponse = objectMapper.createObjectNode().put("id", id);
        try {
            return objectMapper.writeValueAsString(jsonResponse);
        } catch (IOException e) {
            throw new EdcException(e.getMessage());
        }
    }
}
