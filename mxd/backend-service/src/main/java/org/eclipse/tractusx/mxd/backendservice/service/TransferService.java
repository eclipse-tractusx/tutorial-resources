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

import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.tractusx.mxd.backendservice.entity.Transfer;
import org.eclipse.tractusx.mxd.backendservice.entity.TransferResponse;

import java.util.List;

public interface TransferService {

    ServiceResult<Transfer> create(Transfer asset, Monitor monitor);

    List<TransferResponse> getAllTransfer();

    StoreResult<TransferResponse> getTransfer(String transferId);

    StoreResult<TransferResponse> getTransferContent(String transferId);

}
