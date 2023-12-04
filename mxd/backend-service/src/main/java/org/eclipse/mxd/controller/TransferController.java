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


package org.eclipse.mxd.controller;

import org.eclipse.mxd.service.Impl.TransferServiceImpl;
import org.eclipse.mxd.service.TransferService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1/transfers")
@ApplicationScoped
public class TransferController {

    TransferService transfersAPIService = new TransferServiceImpl();

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response acceptTransfer(String transferRequest) {
        return this.transfersAPIService.acceptTransfer(transferRequest);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfer(@PathParam("id") String id) {
        return this.transfersAPIService.getTransfer(id);
    }

    @GET
    @Path("/{id}/contents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransferContents(@PathParam("id") String id) {
        return this.transfersAPIService.getTransferContents(id);
    }

}
