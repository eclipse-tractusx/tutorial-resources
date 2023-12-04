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

package org.eclipse.mxd.service.Impl;

import jakarta.ws.rs.core.MediaType;
import org.eclipse.mxd.entity.Transfer;
import org.eclipse.mxd.model.*;
import org.eclipse.mxd.repository.Impl.TransferRepositoryImpl;
import org.eclipse.mxd.repository.TransferRepository;
import org.eclipse.mxd.service.HttpServiceConnection;
import org.eclipse.mxd.service.TransferService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;


@ApplicationScoped
public class TransferServiceImpl implements TransferService {

    private static final Logger logger = Logger.getLogger(TransferServiceImpl.class.getName());

    private final TransferRepository transferRepository = new TransferRepositoryImpl();

    @Override
    public Response acceptTransfer(String transferRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TransferRequest transferRequestData = objectMapper.readValue(transferRequest, TransferRequest.class);
            String assetsUrl = HttpServiceConnection.getUrlAssets(transferRequestData);
            Long id = this.transferRepository.createTransferWithID(new ObjectMapper().writeValueAsString(transferRequestData), assetsUrl,
                    transferRequestData.getId());
            if (id != -1) {
                TransferModelsResponse transferModelsResponse = new TransferModelsResponse();
                transferModelsResponse.setAsset(new ObjectMapper().readTree(transferRequest));
                return Response.ok(new ObjectMapper().writeValueAsString(transferModelsResponse.getAsset())).build();
            } else {
                ErrorModel errorModel = new ErrorModel(400, "Resource Not Created", "Requested Resource Not Created");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            try {
                logger.info(e.getMessage());
                ErrorModel errorModel = new ErrorModel(500, "Internal Server Error", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } catch (Exception ex) {
                logger.info(ex.getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Internal Server Error")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }

    }

    @Override
    public Response getTransfer(String id) {
        try {
            TransfersModel transfersModel = this.transferRepository.getTransferById(id);
            if (transfersModel != null && transfersModel.getId() != null) {
                JsonNode jsonNode = new ObjectMapper().readTree(transfersModel.getAsset());
                TransferModelsResponse transferModelsResponse = new TransferModelsResponse();
                transferModelsResponse.setAsset(jsonNode);
                return Response.ok(new ObjectMapper().writeValueAsString(transferModelsResponse.getAsset())).build();
            } else {
                ErrorModel errorModel = new ErrorModel(404, "Resource Not Found", "Requested Resource Not Found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            try {
                logger.info(e.getMessage());
                ErrorModel errorModel = new ErrorModel(500, "Internal Server Error", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } catch (Exception ex) {
                logger.info(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Internal Server Error")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }
    }

    @Override
    public Response getTransferContents(String id) {
        try {
            TransfersModel transfersModel = this.transferRepository.getTransferById(id);
            if (transfersModel != null && transfersModel.getContents() != null) {
                JsonNode jsonNode = new ObjectMapper().readTree(transfersModel.getContents());
                TransferModelsResponse transferModelsResponse = new TransferModelsResponse();
                transferModelsResponse.setAsset(jsonNode);
                return Response.ok(new ObjectMapper().writeValueAsString(transferModelsResponse.getAsset())).build();
            } else {
                ErrorModel errorModel = new ErrorModel(404, "Resource Not Found", "Requested Resource Not Found ");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            try {
                logger.info(e.getMessage());
                ErrorModel errorModel = new ErrorModel(500, "Internal Server Error", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } catch (Exception ex) {
                logger.info(ex.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Internal Server Error")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        }
    }
}
