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
import org.eclipse.edc.http.spi.EdcHttpClient;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.tractusx.mxd.backendservice.entity.Transfer;
import org.eclipse.tractusx.mxd.backendservice.entity.TransferResponse;
import org.eclipse.tractusx.mxd.backendservice.store.TransferStoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class TransferServiceImpl implements TransferService {

    private final TransferStoreService transferStoreService;

    private final EdcHttpClient edcHttpClient;

    private final Monitor monitor;

    private final HttpConnectionService httpConnectionService;

    public TransferServiceImpl(TransferStoreService transferStoreService, EdcHttpClient edcHttpClient, Monitor monitor, HttpConnectionService httpConnectionService) {
        this.transferStoreService = transferStoreService;
        this.edcHttpClient = edcHttpClient;
        this.monitor = monitor;
        this.httpConnectionService = httpConnectionService;
    }

    @Override
    public ServiceResult<Transfer> create(Transfer asset, Monitor monitor) {
        String content = httpConnectionService.getUrlAssets(asset, monitor);
        transferStoreService.save(asset, content);
        return ServiceResult.success(asset);
    }

    @Override
    public List<TransferResponse> getAllTransfer() {
        QuerySpec querySpec = new QuerySpec();
        Stream<TransferResponse> transferResponse = this.transferStoreService.findAll(querySpec);
        List<TransferResponse> response = new ArrayList<TransferResponse>();
        transferResponse.forEach(data -> {
            TransferResponse transferResp = TransferResponse.builder()
                    .transferID(data.getTransferID())
                    .asset(data.getAsset())
                    .contents(data.getContents())
                    .updatedDate(data.getUpdatedDate())
                    .createdDate(data.getCreatedDate())
                    .build();
            response.add(transferResp);
        });
        return response;
    }

    @Override
    public ServiceResult<TransferResponse> getTransfer(String transferId) {
        StoreResult<TransferResponse> response = this.transferStoreService.findById(transferId);
        return ServiceResult.success(response.getContent());
    }

    @Override
    public ServiceResult<TransferResponse> getTransferContent(String transferId) {
        StoreResult<TransferResponse> response = this.transferStoreService.findById(transferId);
        return ServiceResult.success(response.getContent());
    }

}
