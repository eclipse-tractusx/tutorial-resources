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

package org.eclipse.tractusx.mxd.backendservice.service;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.tractusx.mxd.backendservice.entity.ContentResponse;
import org.eclipse.tractusx.mxd.backendservice.store.ContentStoreService;
import org.eclipse.tractusx.mxd.util.RandomWordUtil;

import java.util.List;

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
    public List<String> getAllContent() {
        return contentStoreService.findAll(new QuerySpec()).map(ContentResponse::getData).toList();
    }

    @Override
    public ServiceResult<ContentResponse> getContent(String contentId) {
        StoreResult<ContentResponse> response = contentStoreService.findById(contentId);
        return ServiceResult.success(response.getContent());
    }

    @Override
    public String getRandomContent(int size) {
        return RandomWordUtil.generateRandom(size);
    }

    @Override
    public String createRandomContent(int size) {
        Object content = RandomWordUtil.generateRandom(size);
        return contentStoreService.save(content);
    }
}
