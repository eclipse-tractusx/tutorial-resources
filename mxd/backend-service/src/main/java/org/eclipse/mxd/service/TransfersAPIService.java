package org.eclipse.mxd.service;

import org.eclipse.mxd.model.TransferRequest;

import javax.ws.rs.core.Response;

public interface TransfersAPIService {

	Response acceptTransfer(TransferRequest transferRequest);

	Response getTransfer(String id);

	Response getTransferContents(String id);

}
