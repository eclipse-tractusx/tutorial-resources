
/*******************************************************************************
 *
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.service.Impl;

import jakarta.ws.rs.core.MediaType;
import org.eclipse.tractusx.model.*;
import org.eclipse.tractusx.repository.Impl.TransferRepositoryImpl;
import org.eclipse.tractusx.repository.TransferRepository;
import org.eclipse.tractusx.service.HttpConnectionService;
import org.eclipse.tractusx.service.TransferService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.eclipse.tractusx.util.Constants;
import org.eclipse.tractusx.util.SingletonObjectMapper;

import java.util.logging.Logger;


@ApplicationScoped
public class TransferServiceImpl implements TransferService {

    private static final Logger logger = Logger.getLogger(TransferServiceImpl.class.getName());

    private final TransferRepository transferRepository = new TransferRepositoryImpl();

    @Override
    public Response acceptTransfer(String transferRequest) {
        ObjectMapper objectMapper = SingletonObjectMapper.getObjectMapper();
        try {
            TransfersRequest transfersRequestData = objectMapper.readValue(transferRequest, TransfersRequest.class);
            String assetsUrl = HttpConnectionService.getUrlAssets(transfersRequestData);
            String transferId = this.transferRepository.createTransferWithID(SingletonObjectMapper.getObjectMapper().writeValueAsString(transfersRequestData), assetsUrl,
                    transfersRequestData.getId());
            if (!Constants.EMPTYSTRING.equalsIgnoreCase(transferId) && !transferId.isEmpty()) {
                return Response.ok(SingletonObjectMapper.getObjectMapper().writeValueAsString((SingletonObjectMapper.getObjectMapper().readTree(transferRequest)))).build();
            } else {
                ErrorModel errorModel = new ErrorModel(400, "Resource Not Created", "Requested Resource Not Created");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(SingletonObjectMapper.getObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            try {
                logger.info(e.getMessage());
                ErrorModel errorModel = new ErrorModel(500, "Internal Server Error", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(SingletonObjectMapper.getObjectMapper().writeValueAsString(errorModel))
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
            TransfersResponse transfersResponse = this.transferRepository.getTransferById(id);
            logger.info("transferImpl : "+transfersResponse);
            if (transfersResponse != null && transfersResponse.getTransferID() != null) {
                JsonNode jsonNode = SingletonObjectMapper.getObjectMapper().readTree(transfersResponse.getAsset());
                return Response.ok(SingletonObjectMapper.getObjectMapper().writeValueAsString(jsonNode)).build();
            } else {
                ErrorModel errorModel = new ErrorModel(404, "Resource Not Found", "Requested Resource Not Found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(SingletonObjectMapper.getObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            try {
                logger.info(e.getMessage());
                ErrorModel errorModel = new ErrorModel(500, "Internal Server Error", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(SingletonObjectMapper.getObjectMapper().writeValueAsString(errorModel))
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
            TransfersResponse transfersResponse = this.transferRepository.getTransferById(id);
            if (transfersResponse != null && transfersResponse.getContents() != null) {
                JsonNode jsonNode = SingletonObjectMapper.getObjectMapper().readTree(transfersResponse.getContents());
                return Response.ok(SingletonObjectMapper.getObjectMapper().writeValueAsString(jsonNode)).build();
            } else {
                ErrorModel errorModel = new ErrorModel(404, "Resource Not Found", "Requested Resource Not Found ");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(SingletonObjectMapper.getObjectMapper().writeValueAsString(errorModel))
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
        } catch (Exception e) {
            try {
                logger.info(e.getMessage());
                ErrorModel errorModel = new ErrorModel(500, "Internal Server Error", e.getMessage());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(SingletonObjectMapper.getObjectMapper().writeValueAsString(errorModel))
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
