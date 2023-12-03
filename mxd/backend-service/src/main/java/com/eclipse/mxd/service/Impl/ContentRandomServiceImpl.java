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

import com.eclipse.mxd.repository.ContentRamdomRepository;
import com.eclipse.mxd.repository.Impl.ContentRandomRepositoryImpl;
import com.eclipse.mxd.service.ContentRandomService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@ApplicationScoped
public class ContentRandomServiceImpl implements ContentRandomService {

    private static final Logger logger = Logger.getLogger(ContentRandomServiceImpl.class.getName());

    private final ContentRamdomRepository  contentRamdomRepository = new ContentRandomRepositoryImpl();

    @Override
    public Response getRandomContent() {
        try {
            return contentRamdomRepository.getRandomContent();
        }catch (Exception e){
            logger.info(e.getMessage());
            return Response.ok(e.getMessage()).build();
        }
    }
}
