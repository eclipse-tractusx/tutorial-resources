package com.eclipse.mxd.sercvice;

import com.eclipse.mxd.model.TransferRequest;
import jakarta.ws.rs.core.Response;

public interface TransferService {
    Response acceptTransfer(String transferRequest);

    Response getTransfer(String id);

    Response getTransferContents(String id);
}
