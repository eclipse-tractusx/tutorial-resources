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

package com.eclipse.mxd.service.Impl;

import com.eclipse.mxd.model.ContentModelResponse;
import com.eclipse.mxd.model.ContentsModel;
import com.eclipse.mxd.repository.ContentServiceRepository;
import com.eclipse.mxd.repository.Impl.ContentRepositoryImpl;
import com.eclipse.mxd.service.ContentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@ApplicationScoped
public class ContentServiceImpl implements ContentService {

    private static final Logger logger = Logger.getLogger(ContentServiceImpl.class.getName());

    private final ContentServiceRepository contentServiceRepository = new ContentRepositoryImpl();

    @Override
    public Response getAll() {
        try {
            List<ContentsModel> contentResponse = this.contentServiceRepository.getAllAssets();
            if (contentResponse != null && !contentResponse.isEmpty()) {
                List<ContentModelResponse> contentsModels = new ArrayList<ContentModelResponse>();
                for (ContentsModel contentsModel : contentResponse) {
                    JsonNode jsonNode = new ObjectMapper().readTree(contentsModel.getAsset());
                    ContentModelResponse contentsModel1 = new ContentModelResponse();
                    contentsModel1.setId(contentsModel.getId());
                    contentsModel1.setAsset(jsonNode);
                    contentsModel1.setCreatedDate(contentsModel.getCreatedDate());
                    contentsModel1.setUpdatedDate(contentsModel.getUpdatedDate());
                    contentsModels.add(contentsModel1);
                }
                return Response.ok(new ObjectMapper().writeValueAsString(contentsModels)).build();

            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset Not Found").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response getById(Long id) {
        try {
            ContentsModel content = this.contentServiceRepository.getAssetById(id);
            if (content != null && content.getId()!=null) {
                JsonNode jsonNode = new ObjectMapper().readTree(content.getAsset());
                ContentModelResponse contentModelRes = new ContentModelResponse();
                contentModelRes.setId(content.getId());
                contentModelRes.setCreatedDate(content.getCreatedDate());
                contentModelRes.setUpdatedDate(content.getUpdatedDate());
                contentModelRes.setAsset(jsonNode);
                return Response.ok(new ObjectMapper().writeValueAsString(contentModelRes)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset Not Found With ID").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }

    @Override
    public Response postContent(String requestBody, UriInfo uriInfo) {
        try {
            Long id = contentServiceRepository.createAsset(requestBody);
            if (id != null) {
                logger.info(uriInfo.getAbsolutePath() + "");
                return Response.ok(uriInfo.getAbsolutePath() + "" + id).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Asset Not Created").build();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.serverError().entity("Internal server error").build();
        }
    }
}
