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

package org.eclipse.tractusx.mxd.backendservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.mxd.backendservice.service.ContentService;
import org.eclipse.tractusx.mxd.util.Constants;
import org.eclipse.tractusx.mxd.util.Converter;

import java.io.IOException;
import java.util.Optional;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1/contents")
public class ContentApiController {

    private final Monitor monitor;

    private final ContentService service;

    private final ObjectMapper objectMapper;

    private final String host;
    private final String port;

    public ContentApiController(ContentService service, Monitor monitor, ObjectMapper objectMapper,
                                String host, String port) {
        this.service = service;
        this.monitor = monitor;
        this.objectMapper = objectMapper;
        this.host = host;
        this.port = port;
    }

    @POST
    public String createContent(Object contentJson) {
        var contentID = this.service.create(contentJson);
        return createJsonResponse(contentID);
    }

    @GET
    public String getAllContent() {
        return this.service.getAllContent().toString();
    }

    @GET
    @Path("/{contentId}")
    public String getContentByID(@PathParam("contentId") String contentId) {
        return Optional.of(contentId)
                .map(id -> service.getContent(contentId))
                .map(content -> content.getContent() != null ? content.getContent().getData() : Converter.toJson(content.getFailure(), objectMapper))
                .orElse(Constants.CONTENT_ID_ERROR_MESSAGE);
    }

    @GET
    @Path("/random")
    public String getRandomContent(@QueryParam("size") @DefaultValue("1KB") String size) {
        return parseSize(size)
                .map(sizeInBytes -> this.service.getRandomContent(sizeInBytes))
                .orElse(Constants.CONTENT_SIZE_ERROR_MESSAGE);
    }

    @GET
    @Path("/create/random")
    public String createRandomContent(@QueryParam("size") @DefaultValue("1KB") String size) {
        return parseSize(size)
                .map(sizeInBytes -> this.service.createRandomContent(sizeInBytes))
                .map(this::createJsonResponse)
                .orElse(Constants.CONTENT_SIZE_ERROR_MESSAGE);
    }

    private Optional<Integer> parseSize(String size) {
        try {
            int sizeInBytes;
            if (size.endsWith("KB")) {
                sizeInBytes = Integer.parseInt(size.replace("KB", "").trim()) * 1024;
            } else if (size.endsWith("MB")) {
                sizeInBytes = Integer.parseInt(size.replace("MB", "").trim()) * 1024 * 1024;
            } else {
                return Optional.empty();
            }
            if (sizeInBytes > 10 * 1024 * 1024 || sizeInBytes < 1024) {
                return Optional.empty();
            }

            return Optional.of(sizeInBytes);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private String createJsonResponse(String id) {
        JsonNode jsonResponse = objectMapper.createObjectNode()
                .put("id", id)
                .put("url", UriBuilder.fromUri("http://" + host  +  ":" + port)
                        .path("api")
                        .path("v1")
                        .path("contents")
                        .path(String.valueOf(id))
                        .build().toString());
        try {
            return objectMapper.writeValueAsString(jsonResponse);
        } catch (IOException e) {
            throw new EdcException(e.getMessage());
        }
    }

}
