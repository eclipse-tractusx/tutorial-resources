package org.eclipse.mxd.service.impl;

import org.eclipse.mxd.model.TransferRequest;
import org.eclipse.mxd.model.TransfersModel;
import org.eclipse.mxd.repository.TransfersAPIRepository;
import org.eclipse.mxd.service.HttpServiceConnection;
import org.eclipse.mxd.service.TransfersAPIService;

import com.google.gson.Gson;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Stateless
public class TransfersAPIServiceImpl implements TransfersAPIService {
	
	private static final Logger logger = Logger.getLogger(TransfersAPIServiceImpl.class.getName());

	@Inject
	private TransfersAPIRepository transfersAPIRepository;

	@Override
	public Response acceptTransfer(TransferRequest transferRequest) {
		String assestsUrl = HttpServiceConnection.getUrlAssets(transferRequest);
		try {
			// Create a Gson instance
			Gson gson = new Gson();
			String transferRequestJson = gson.toJson(transferRequest);
			// Assuming receivedModel has appropriate fields such as asset, contents, id
			String id = transfersAPIRepository.createTransferWithID(transferRequestJson, assestsUrl,
					transferRequest.getId());
			// Additional logic if needed
			return Response.ok(id).build();

		} catch (Exception e) {
			logger.info(e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity("Not Created").build();
		}
	}

	@Override
	public Response getTransfer(String id) {
		try {
			TransfersModel transfersModel = transfersAPIRepository.getTransferById(id);
			if (transfersModel != null) {
				return Response.ok(transfersModel).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Transfer not found").build();
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			return Response.serverError().entity("Internal server error").build();
		}
	}

	@Override
	public Response getTransferContents(String id) {
		try {
			TransfersModel transfersModel = transfersAPIRepository.getTransferById(id);
			if (transfersModel != null) {
				return Response.ok(transfersModel.getContents()).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Transfer not found").build();
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			return Response.serverError().entity("Internal server error").build();
		}
	}
}
