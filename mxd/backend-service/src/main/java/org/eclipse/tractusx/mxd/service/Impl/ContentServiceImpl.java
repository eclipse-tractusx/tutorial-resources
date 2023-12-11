
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

package org.eclipse.tractusx.mxd.service.Impl;

import jakarta.ws.rs.core.MediaType;
import org.eclipse.tractusx.model.*;
import org.eclipse.tractusx.mxd.model.ContentGetAllResponse;
import org.eclipse.tractusx.mxd.model.ContentsRequest;
import org.eclipse.tractusx.mxd.model.ContentsResponse;
import org.eclipse.tractusx.mxd.model.ErrorModel;
import org.eclipse.tractusx.mxd.repository.ContentsRepository;
import org.eclipse.tractusx.mxd.repository.Impl.ContentsRepositoryImpl;
import org.eclipse.tractusx.mxd.service.ContentService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.tractusx.mxd.util.SingletonObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@ApplicationScoped
public class ContentServiceImpl implements ContentService {

    private static final Logger logger = Logger.getLogger(ContentServiceImpl.class.getName());

    private final ContentsRepository contentServiceRepository = new ContentsRepositoryImpl();

    @Override
    public Response getAll(UriInfo uriInfo) {
        try {
            List<ContentsRequest> contentResponse = this.contentServiceRepository.getAllAssets();
            if (contentResponse != null && !contentResponse.isEmpty()) {
                List<ContentGetAllResponse> contentsModels = new ArrayList<ContentGetAllResponse>();
                for (ContentsRequest contentsRequest : contentResponse) {
                    JsonNode jsonNode = SingletonObjectMapper.getObjectMapper().readTree(contentsRequest.getAsset());
                    ContentGetAllResponse contentsModelRes = new ContentGetAllResponse();
                    contentsModelRes.setId(contentsRequest.getId());
                    contentsModelRes.setAsset(jsonNode);
                    contentsModelRes.setUrl(uriInfo.getAbsolutePath().toString() + contentsRequest.getId());
                    contentsModelRes.setCreatedDate(contentsRequest.getCreatedDate());
                    contentsModelRes.setUpdatedDate(contentsRequest.getUpdatedDate());
                    contentsModels.add(contentsModelRes);
                }
                return Response.ok(SingletonObjectMapper.getObjectMapper().writeValueAsString(contentsModels)).build();

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
    public Response getById(Long id, UriInfo uriInfo) {
        try {
            ContentsRequest content = this.contentServiceRepository.getAssetById(id);
            if (content != null && content.getId() != null) {
                return Response.ok(SingletonObjectMapper.getObjectMapper().writeValueAsString(SingletonObjectMapper.getObjectMapper().readTree(content.getAsset()))).build();
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
    public Response postContent(String requestBody, UriInfo uriInfo) {
        try {
            Long id = contentServiceRepository.createAsset(requestBody);
            logger.info("Saving contentId : " + id);
            if (id != null) {
                logger.info("Path  " + uriInfo.getAbsolutePath() + "");
                ContentsResponse contentsResponse = new ContentsResponse();
                contentsResponse.setId(id);
                contentsResponse.setUrl(uriInfo.getAbsolutePath() + "" + id);
                return Response.ok(SingletonObjectMapper.getObjectMapper().writeValueAsString(contentsResponse)).build();
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
}
