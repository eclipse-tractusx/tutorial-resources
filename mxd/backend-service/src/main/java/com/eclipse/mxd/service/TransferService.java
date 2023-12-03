package com.eclipse.mxd.service;

import jakarta.ws.rs.core.Response;

public interface TransferService {
    Response acceptTransfer(String transferRequest);

    Response getTransfer(String id);

    Response getTransferContents(String id);
}
