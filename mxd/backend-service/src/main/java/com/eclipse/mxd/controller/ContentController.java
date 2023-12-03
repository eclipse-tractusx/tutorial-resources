
/*******************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *    Contributors:Ravinder Kumar
 *    Backend-API and implementation
 * 
 ******************************************************************************/

package com.eclipse.mxd.controller;

import com.eclipse.mxd.service.ContentRandomService;
import com.eclipse.mxd.service.ContentService;
import com.eclipse.mxd.service.Impl.ContentRandomServiceImpl;
import com.eclipse.mxd.service.Impl.ContentServiceImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;


@Path("/v1/content")
@ApplicationScoped
public class ContentController {
    private final ContentService contentService = new ContentServiceImpl();

    private final ContentRandomService contentRandomService = new ContentRandomServiceImpl();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return this.contentService.getAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id) {
        return contentService.getById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postContent(String requestBody, @Context UriInfo uriInfo) {
        return this.contentService.postContent(requestBody, uriInfo);
    }

    @GET
    @Path("/random")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRandomContent() {
        return this.contentRandomService.getRandomContent();
    }
}
