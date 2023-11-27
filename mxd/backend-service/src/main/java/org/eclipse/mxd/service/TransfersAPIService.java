package org.eclipse.mxd.service;

import org.eclipse.mxd.model.ReceivedModel;

import javax.ws.rs.core.Response;

public interface TransfersAPIService {
	
	Response acceptTransfer(ReceivedModel receivedModel);

	Response getTransfer(String id);

	Response getTransferContents(String id);

	Response test();
}
