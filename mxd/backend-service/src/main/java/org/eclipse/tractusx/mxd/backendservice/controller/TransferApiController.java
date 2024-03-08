/*******************************************************************************
 *
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 ******************************************************************************/

package org.eclipse.tractusx.mxd.backendservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.mxd.backendservice.entity.Transfer;
import org.eclipse.tractusx.mxd.backendservice.entity.TransferRequest;
import org.eclipse.tractusx.mxd.backendservice.entity.TransferResponse;
import org.eclipse.tractusx.mxd.backendservice.service.TransferService;
import org.eclipse.tractusx.mxd.util.Constants;
import org.eclipse.tractusx.mxd.util.Converter;

import java.util.List;
import java.util.Optional;


@Consumes(MediaType.APPLICATION_JSON)
@Produces((MediaType.APPLICATION_JSON))
@Path("/v1/transfers")
public class TransferApiController {

    private final TransferService transferService;
    private final Monitor monitor;

    private final ObjectMapper objectMapper;

    public TransferApiController(TransferService transferService,Monitor monitor,ObjectMapper objectMapper) {
        this.transferService = transferService;
        this.monitor = monitor;
        this.objectMapper=objectMapper;
    }

    @POST
    @Produces((MediaType.APPLICATION_JSON))
    @Consumes((MediaType.APPLICATION_JSON))
    public TransferRequest insertTransfer(TransferRequest transferRequest) {
        monitor.info("insertTransfer POST request "+transferRequest);

        Transfer transfer = new Transfer.Builder()
                .id(transferRequest.getId())
                .endpoint(transferRequest.getEndpoint())
                .authKey(transferRequest.getAuthKey())
                .authCode(transferRequest.getAuthCode())
                .properties(transferRequest.getProperties())
                .build();
        var transferResponse = this.transferService.create(transfer,monitor)
                .map(a -> TransferRequest.builder()
                        .id(a.getId())
                        .endpoint(a.getEndpoint())
                        .authKey(a.getAuthKey())
                        .authCode(a.getAuthCode())
                        .properties(a.getProperties())
                        .build());
        return transferResponse.getContent();

    }

    @GET
    public List<TransferResponse> getAllTransfer() {
        return this.transferService.getAllTransfer();
    }

    @GET
    @Path("/{transferId}")
    public String getTransfer(@PathParam("transferId") String transferId) {
        return Optional.of(transferId)
                .map(id -> transferService.getTransfer(transferId))
                .map(transfer -> transfer.getContent() != null ? transfer.getContent().getAsset() : Converter.toJson(transfer.getFailure(),objectMapper))
                .orElse(Constants.DEFAULTERRORMESSAGE);
    }

    @GET
    @Path("/{id}/contents")
    public Object getTransferContents(@PathParam("id") String transferId) {
        return Optional.of(transferId)
                .map(id -> transferService.getTransfer(transferId))
                .map(transfer -> transfer.getContent() != null ? transfer.getContent().getContents() : Converter.toJson(transfer.getFailure(),objectMapper))
                .orElse(Constants.DEFAULTERRORMESSAGE);
    }
}
