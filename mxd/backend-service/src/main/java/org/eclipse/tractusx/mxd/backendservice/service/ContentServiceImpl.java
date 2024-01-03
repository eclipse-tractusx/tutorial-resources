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

package org.eclipse.tractusx.mxd.backendservice.service;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.tractusx.mxd.backendservice.entity.ContentResponse;
import org.eclipse.tractusx.mxd.backendservice.store.ContentStoreService;
import org.eclipse.tractusx.mxd.util.RandomWordUtil;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ContentServiceImpl implements ContentService {

    private final ContentStoreService contentStoreService;
    private final Monitor monitor;

    public ContentServiceImpl(ContentStoreService contentStoreService, Monitor monitor) {
        this.contentStoreService = contentStoreService;
        this.monitor = monitor;
    }

    @Override
    public String create(Object content) {
        return contentStoreService.save(content);
    }

    @Override
    public String getAllContent() {
        return contentStoreService.findAll(new QuerySpec()).map(ContentResponse::getData).toList().toString();
    }

    @Override
    public StoreResult<ContentResponse> getContent(String contentId) {
        return contentStoreService.findById(contentId);
    }

    @Override
    public String getRandomContent() {
        return RandomWordUtil.generateRandom();
    }
}
